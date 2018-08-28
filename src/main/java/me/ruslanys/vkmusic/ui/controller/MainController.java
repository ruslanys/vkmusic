package me.ruslanys.vkmusic.ui.controller;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
//@Component
//@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
/*
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
        DownloadProperties properties = propertyService.get(DownloadProperties.class);
        if (properties.getDestination() == null) {
            chooseDestination();
        }

        displayTray();
        initComponents();
//        mainFrame.setAutoSync(properties.isAutoSync());
        mainFrame.setVisible(false);
    }

    @Override
    public void chooseDestination() {
        DownloadProperties properties = propertyService.get(DownloadProperties.class);
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
        DownloadProperties properties = propertyService.get(DownloadProperties.class);
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
*/
