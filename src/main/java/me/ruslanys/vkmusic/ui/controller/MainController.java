package me.ruslanys.vkmusic.ui.controller;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.entity.domain.DownloadStatus;
import me.ruslanys.vkmusic.entity.domain.event.DownloadFinishEvent;
import me.ruslanys.vkmusic.entity.domain.event.DownloadStatusEvent;
import me.ruslanys.vkmusic.entity.domain.event.LogoutEvent;
import me.ruslanys.vkmusic.property.DownloaderProperties;
import me.ruslanys.vkmusic.services.AudioService;
import me.ruslanys.vkmusic.services.DownloadService;
import me.ruslanys.vkmusic.services.PropertyService;
import me.ruslanys.vkmusic.ui.view.LoadingFrame;
import me.ruslanys.vkmusic.ui.view.MainFrame;
import me.ruslanys.vkmusic.util.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MainController implements Runnable, MainFrame.OnSyncListener, MainFrame.OnChangeDestinationListener {

    private static final String DEFAULT_STATUS = "OK";

    private final MainFrame mainFrame;

    private final AudioService audioService;
    private final DownloadService downloadService;
    private final PropertyService propertyService;
    private final ScheduledExecutorService executor;

    private final AtomicLong counter = new AtomicLong();

    private volatile ScheduledFuture syncFuture;

    @Autowired
    public MainController(@NonNull MainFrame mainFrame,
                          @NonNull AudioService audioService,
                          @NonNull DownloadService downloadService,
                          @NonNull PropertyService propertyService,
                          @NonNull ScheduledExecutorService executor) {
        this.mainFrame = mainFrame;
        this.audioService = audioService;
        this.downloadService = downloadService;
        this.propertyService = propertyService;
        this.executor = executor;


        mainFrame.setSyncListener(this);
        mainFrame.setDestinationListener(this);
    }

    @Override
    public void run() {
        DownloaderProperties properties = propertyService.get(DownloaderProperties.class);
        if (properties.getDestination() == null) {
            chooseDestination();
        }
        
        displayTray();
        initComponents();
        mainFrame.setAutoSync(properties.isAutoSync());
    }

    @Override
    public void chooseDestination() {
        DownloaderProperties properties = propertyService.get(DownloaderProperties.class);
        JFileChooser chooser = new JFileChooser(properties.getDestination());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showDialog(mainFrame, "Choose") == JFileChooser.APPROVE_OPTION) {
            properties.setDestination(chooser.getSelectedFile().toString());
            propertyService.set(properties);
        } else if (properties.getDestination() == null) {
            System.exit(1);
        }
    }

    private void initComponents() {
        mainFrame.setStatus(DEFAULT_STATUS);
        mainFrame.setState(LoadingFrame.State.LOADING);
        mainFrame.setVisible(true);

        executor.execute(this::loadEntities);
    }

    private List<Audio> loadEntities() {
        List<Audio> audioList = audioService.fetchAll();
        mainFrame.getModel().add(audioList);
        mainFrame.setState(LoadingFrame.State.MAIN); // needs only once

        return audioList;
    }

    private void sync() {
        List<Audio> entities = loadEntities();
        entities.removeIf(e -> e.getStatus() != DownloadStatus.NEW);

        download(entities);
    }

    private void syncFailed() {
        List<Audio> failed = audioService.findFailed();
        download(failed);
    }

    @Override
    public void onSync() {
        executor.execute(this::sync);
    }

    @Override
    public void onSyncFailed() {
        executor.execute(this::syncFailed);
    }

    @Override
    public void updateAutoSyncState(boolean state) {
        DownloaderProperties properties = propertyService.get(DownloaderProperties.class);
        properties.setAutoSync(state);
        propertyService.set(properties);

        if (syncFuture != null) {
            syncFuture.cancel(true);
            syncFuture = null;
        }

        if (state) {
            mainFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            mainFrame.setVisible(false);

            syncFuture = executor.scheduleWithFixedDelay(this::sync, 0, properties.getAutoSyncDelay(),
                    TimeUnit.SECONDS);
        } else {
            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
    }

    private synchronized void download(List<Audio> audios) {
        if (audios.isEmpty()) return;

        Notifications.showNotification(String.format("Доступно для загрузки: %d", audios.size()));
        mainFrame.setStatus("Синхронизация...");

        // --
        counter.set(audios.size());
        downloadService.download(audios);
    }

    @SneakyThrows
    private void displayTray() {
        SystemTray tray = SystemTray.getSystemTray();
        if (tray.getTrayIcons().length > 0) {
            return;
        }

        TrayIcon trayIcon = new TrayIcon(ImageIO.read(getClass().getClassLoader().getResource("images/tray/base.png")), "VKMusic");
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(e -> mainFrame.setVisible(true));

        tray.add(trayIcon);
    }

    private void hideTray() {
        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon[] icons = tray.getTrayIcons();

        for (TrayIcon icon : icons) {
            tray.remove(icon);
        }
    }

    @Async
    @EventListener
    public void onDownloadStatusEvent(DownloadStatusEvent event) {
        mainFrame.setStatus(String.format("В очереди на загрузку: %d", counter.decrementAndGet()));

        Audio audio = event.getAudio();
        mainFrame.getModel().get(audio.getId()).setStatus(event.getStatus());
        mainFrame.getModel().fireTableRowsUpdated(audio.getPosition() - 1, audio.getPosition() - 1);
    }

    @EventListener
    public void onDownloadFinishEvent(DownloadFinishEvent event) {
        mainFrame.setStatus(DEFAULT_STATUS);
        Notifications.showNotification("Синхронизация завершена, обработано " + event.getAudioList().size() + ".");
    }

    @EventListener
    public void onLogout(LogoutEvent event) {
        mainFrame.getModel().clear();
        hideTray();
    }

}
