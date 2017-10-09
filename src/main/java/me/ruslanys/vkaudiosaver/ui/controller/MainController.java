package me.ruslanys.vkaudiosaver.ui.controller;

import lombok.NonNull;
import me.ruslanys.vkaudiosaver.component.impl.TrayHandler;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.domain.event.AudioUpdatedEvent;
import me.ruslanys.vkaudiosaver.domain.event.TrayStateEvent;
import me.ruslanys.vkaudiosaver.services.AudioService;
import me.ruslanys.vkaudiosaver.services.DownloadService;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import me.ruslanys.vkaudiosaver.ui.view.LoadingFrame;
import me.ruslanys.vkaudiosaver.ui.view.MainFrame;
import me.ruslanys.vkaudiosaver.util.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class MainController implements Runnable, ApplicationListener<AudioUpdatedEvent> {

    private final MainFrame mainFrame;

    private final ApplicationEventPublisher publisher;
    private final AudioService audioService;
    private final DownloadService downloadService;

    private final PropertyService propertyService;

    private final AtomicLong counter = new AtomicLong();

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

        mainFrame.setStatus(propertyService.getVkProperties().getUsername());
        mainFrame.setVisible(true);

        loadAudio();
    }

    private void loadAudio() {
        mainFrame.setState(LoadingFrame.State.LOADING);

        CompletableFuture
                .supplyAsync(audioService::getAll)
                .thenApply(audios -> {
                    mainFrame.getModel().addEntities(audios);
                    mainFrame.setState(LoadingFrame.State.MAIN);

                    return audios;
                })
                .thenAccept(this::download);
    }

    private void download(List<Audio> audioList) {
        mainFrame.setStatus("Синхронизация...");
        counter.set(audioList.stream().filter(a -> a.getStatus() != Audio.Status.DOWNLOADED).count());
        downloadService.download(audioList);
    }

    @Override
    public void onApplicationEvent(AudioUpdatedEvent event) {
        long queueSize = counter.decrementAndGet();
        if (queueSize > 0) {
            mainFrame.setStatus(String.format("В очереди на загрузку: %d", queueSize));
        } else {
            String status = "Синхронизация завершена.";
            mainFrame.setStatus(status);
            Notifications.showNotification(status);
        }

        mainFrame.getModel().fireTableDataChanged();
    }

}
