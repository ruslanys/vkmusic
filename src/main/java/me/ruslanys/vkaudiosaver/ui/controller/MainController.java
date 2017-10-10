package me.ruslanys.vkaudiosaver.ui.controller;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.entity.Audio;
import me.ruslanys.vkaudiosaver.entity.domain.DownloadStatus;
import me.ruslanys.vkaudiosaver.entity.domain.event.DownloadFinishEvent;
import me.ruslanys.vkaudiosaver.entity.domain.event.DownloadStatusEvent;
import me.ruslanys.vkaudiosaver.entity.domain.event.LogoutEvent;
import me.ruslanys.vkaudiosaver.property.VkProperties;
import me.ruslanys.vkaudiosaver.services.AudioService;
import me.ruslanys.vkaudiosaver.services.DownloadService;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import me.ruslanys.vkaudiosaver.ui.view.LoadingFrame;
import me.ruslanys.vkaudiosaver.ui.view.MainFrame;
import me.ruslanys.vkaudiosaver.util.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MainController implements Runnable {

    private final MainFrame mainFrame;

    private final AudioService audioService;
    private final DownloadService downloadService;

    private final PropertyService propertyService;

    private final AtomicLong counter = new AtomicLong();
    private ScheduledExecutorService scheduler;

    @Autowired
    public MainController(@NonNull MainFrame mainFrame,
                          @NonNull AudioService audioService,
                          @NonNull DownloadService downloadService,
                          @NonNull PropertyService propertyService) {
        this.mainFrame = mainFrame;
        this.audioService = audioService;
        this.downloadService = downloadService;
        this.propertyService = propertyService;
    }

    @Override
    public void run() {
        displayTray();

        mainFrame.setStatus(propertyService.get(VkProperties.class).getUsername());
        mainFrame.setVisible(true);

        loadAudio();
    }

    private void loadAudio() {
        mainFrame.setState(LoadingFrame.State.LOADING);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture.supplyAsync(audioService::fetchAll, executor)
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
        executor.shutdown();
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

    @SneakyThrows
    private void displayTray() {
        SystemTray tray = SystemTray.getSystemTray();

        TrayIcon trayIcon = new TrayIcon(ImageIO.read(getClass().getClassLoader().getResource("images/tray/base.png")), "VKMusic");
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(e -> mainFrame.setVisible(true));

        tray.add(trayIcon);
    }

    @Async
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
        if (scheduler != null) {
            scheduler.shutdown();
        }
        mainFrame.getModel().clear();
        // TODO: Remove tray icon
    }

}
