package me.ruslanys.vkaudiosaver.ui.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.component.impl.TrayHandler;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.domain.event.AudioUpdatedEvent;
import me.ruslanys.vkaudiosaver.domain.event.LogoutEvent;
import me.ruslanys.vkaudiosaver.domain.event.TrayStateEvent;
import me.ruslanys.vkaudiosaver.property.VkProperties;
import me.ruslanys.vkaudiosaver.services.AudioService;
import me.ruslanys.vkaudiosaver.services.DownloadService;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import me.ruslanys.vkaudiosaver.ui.view.LoadingFrame;
import me.ruslanys.vkaudiosaver.ui.view.MainFrame;
import me.ruslanys.vkaudiosaver.util.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class MainController implements Runnable {

    private final MainFrame mainFrame;

    private final ApplicationEventPublisher publisher;
    private final AudioService audioService;
    private final DownloadService downloadService;

    private final PropertyService propertyService;

    private final AtomicLong counter = new AtomicLong();
    private ScheduledExecutorService scheduler;

    @Autowired
    public MainController(@NonNull MainFrame mainFrame,
                          @NonNull ApplicationEventPublisher publisher,
                          @NonNull AudioService audioService,
                          @NonNull DownloadService downloadService,
                          @NonNull PropertyService propertyService) {
        this.mainFrame = mainFrame;
        this.publisher = publisher;
        this.audioService = audioService;
        this.downloadService = downloadService;
        this.propertyService = propertyService;
    }

    @Override
    public void run() {
        publisher.publishEvent(new TrayStateEvent(this, TrayHandler.State.BASE, e -> mainFrame.setVisible(true)));

        mainFrame.setStatus(propertyService.get(VkProperties.class).getUsername());
        mainFrame.setVisible(true);

        loadAudio();
    }

    private void loadAudio() {
        mainFrame.setState(LoadingFrame.State.LOADING);

        CompletableFuture
                .supplyAsync(audioService::getAll)
                .thenApply(audios -> {
                    mainFrame.getModel().add(audios);
                    mainFrame.setState(LoadingFrame.State.MAIN);

                    return audios;
                })
                .thenAccept(this::download)
                .thenRun(() -> {
                    scheduler = Executors.newSingleThreadScheduledExecutor();
                    scheduler.scheduleAtFixedRate(MainController.this::sync, 0L, 30L, TimeUnit.SECONDS);
                });
    }

    private void sync() {
        List<Audio> audioList = audioService.getAll();
        mainFrame.getModel().add(audioList);
        download(audioList);
    }

    private void download(List<Audio> audioList) {
        long count = audioList.stream().filter(a -> a.getStatus() == Audio.Status.NEW).count();
        if (count == 0) {
            return;
        }

        Notifications.showNotification(String.format("Доступно к загрузке: %d", count));
        mainFrame.setStatus("Синхронизация...");

        // --
        counter.set(count);
        downloadService.download(audioList);
    }

    @EventListener
    public void onAudioUpdated(AudioUpdatedEvent event) {
        long queueSize = counter.decrementAndGet();
        if (queueSize > 0) {
            mainFrame.setStatus(String.format("В очереди на загрузку: %d", queueSize));
        } else {
            mainFrame.setStatus(propertyService.get(VkProperties.class).getUsername());
            Notifications.showNotification("Синхронизация завершена.");
        }

        mainFrame.getModel().fireTableDataChanged();
    }

    @EventListener
    public void onLogout(LogoutEvent event) {
        scheduler.shutdown();
    }

}
