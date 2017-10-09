package me.ruslanys.vkaudiosaver.ui.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.component.impl.TrayHandler;
import me.ruslanys.vkaudiosaver.entity.Audio;
import me.ruslanys.vkaudiosaver.entity.domain.DownloadStatus;
import me.ruslanys.vkaudiosaver.entity.domain.event.DownloadFinishEvent;
import me.ruslanys.vkaudiosaver.entity.domain.event.DownloadStatusEvent;
import me.ruslanys.vkaudiosaver.entity.domain.event.LogoutEvent;
import me.ruslanys.vkaudiosaver.entity.domain.event.TrayStateEvent;
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

import java.util.ArrayList;
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
                .supplyAsync(audioService::fetchAll)
                .thenApply(audios -> {
                    mainFrame.getModel().add(audios);
                    mainFrame.setState(LoadingFrame.State.MAIN);

                    return audios;
                })
                .thenAccept(this::download) // todo: move
                .thenRun(() -> { // todo: move
                    scheduler = Executors.newSingleThreadScheduledExecutor();
                    scheduler.scheduleAtFixedRate(MainController.this::onSync, 0L, 30L, TimeUnit.SECONDS);
                });
    }

    public void onSync() {
        List<Audio> audioList = audioService.fetchAll();
        mainFrame.getModel().add(audioList);
        download(audioList);
    }

    private void download(List<Audio> audios) {
        List<Audio> audioList = new ArrayList<>(audios);
        audioList.removeIf(a -> a.getStatus() != DownloadStatus.NEW);
        if (audioList.isEmpty()) {
            return;
        }

        Notifications.showNotification(String.format("Доступно к загрузке: %d", audioList.size()));
        mainFrame.setStatus("Синхронизация...");

        // --
        counter.set(audioList.size());
        downloadService.download(audioList);
    }

    @EventListener
    public void onDownloadStatusEvent(DownloadStatusEvent event) {
        mainFrame.setStatus(String.format("В очереди на загрузку: %d", counter.decrementAndGet()));

        mainFrame.getModel().get(event.getAudio().getId()).setStatus(event.getStatus());
        mainFrame.getModel().fireTableDataChanged();
    }

    @EventListener
    public void onDownloadFinishEvent(DownloadFinishEvent event) {
        mainFrame.setStatus(propertyService.get(VkProperties.class).getUsername());
        Notifications.showNotification("Синхронизация завершена, обработано " + event.getAudioList().size() + ".");
    }

    @EventListener
    public void onLogout(LogoutEvent event) {
        scheduler.shutdown();
    }

}
