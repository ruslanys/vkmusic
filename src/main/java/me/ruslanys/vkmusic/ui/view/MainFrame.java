package me.ruslanys.vkmusic.ui.view;

import lombok.NonNull;
import lombok.Setter;
import me.ruslanys.vkmusic.entity.domain.event.LogoutEvent;
import me.ruslanys.vkmusic.ui.model.AudioTableModel;
import me.ruslanys.vkmusic.util.DesktopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class MainFrame extends LoadingFrame implements ActionListener, ItemListener {

    private static final String ACTION_SYNC = "SYNC";
    private static final String ACTION_SYNC_FAILED = "SYNC_FAILED";
    private static final String ACTION_CHANGE_DESTINATION = "DESTINATION";

    private final transient ApplicationEventPublisher publisher;
    private final AboutFrame aboutFrame;

    @Setter private transient OnSyncListener syncListener;
    @Setter private transient OnChangeDestinationListener destinationListener;

    private JTable table;
    private AudioTableModel model;
    private JLabel toolbarLabel;

    private JMenu syncMenu;
    private JCheckBoxMenuItem autoSyncItem;
    private JMenuItem startSyncItem;


    @Autowired
    public MainFrame(@NonNull ApplicationEventPublisher publisher,
                     @NonNull AboutFrame aboutFrame) {
        super();
        this.publisher = publisher;
        this.aboutFrame = aboutFrame;

        initMenu();
    }

    @Override
    protected void initWindow() {
        setTitle("VKMusic");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(DesktopUtils.getIcon());

        setSize(new Dimension(640, 480));
        setMinimumSize(new Dimension(640, 240));
        setLocationRelativeTo(null);
    }

    @Override
    protected JPanel initMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));

        final JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);

        table = new JTable() {
            @Override
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        model = new AudioTableModel();
        table.setModel(model);
        table.setTransferHandler(new TransferHandler() {
            @Override
            public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
                int selectedRow = table.getSelectedRow();
                String artist = model.getValueAt(selectedRow, 1).toString();
                String title = model.getValueAt(selectedRow, 2).toString();

                StringSelection selection = new StringSelection(artist + " - " + title);
                clip.setContents(selection, null);
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setPreferredWidth(55);
        table.getColumnModel().getColumn(3).setMaxWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(55);
        table.getColumnModel().getColumn(4).setMaxWidth(70);

        scrollPane.setViewportView(table);

        final JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMargin(new Insets(0, 5, 0, 0));
        panel.add(toolBar, BorderLayout.SOUTH);

        toolbarLabel = new JLabel("...");
        toolBar.add(toolbarLabel);

        return panel;
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(initFileMenu());
        menuBar.add(initSettingsMenu());
        menuBar.add(initSyncMenu());
        menuBar.add(initHelpMenu());

        setJMenuBar(menuBar);
    }

    private JMenu initHelpMenu() {
        JMenu helpMenu = new JMenu("Помощь");

        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> aboutFrame.setVisible(true));
        helpMenu.add(aboutItem);
        return helpMenu;
    }

    private JMenu initSyncMenu() {
        syncMenu = new JMenu("Синхронизация");

        autoSyncItem = new JCheckBoxMenuItem("Автоматическая синхронизация");
        startSyncItem = new JMenuItem("Запуск");
        JMenuItem startFailedSyncItem = new JMenuItem("Запуск проваленных");

        startSyncItem.setActionCommand(ACTION_SYNC);
        startFailedSyncItem.setActionCommand(ACTION_SYNC_FAILED);

        autoSyncItem.addItemListener(this);
        startSyncItem.addActionListener(this);
        startFailedSyncItem.addActionListener(this);

        syncMenu.add(autoSyncItem);
        syncMenu.addSeparator();
        syncMenu.add(startSyncItem);
        syncMenu.add(startFailedSyncItem);

        return syncMenu;
    }

    private JMenu initSettingsMenu() {
        JMenu settingsMenu = new JMenu("Настройки");

        JMenuItem destinationItem = new JMenuItem("Указать папку");
        destinationItem.setActionCommand(ACTION_CHANGE_DESTINATION);
        destinationItem.addActionListener(this);
        settingsMenu.add(destinationItem);
        return settingsMenu;
    }

    private JMenu initFileMenu() {
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem logoutItem = new JMenuItem("Сменить пользователя");
        JMenuItem exitItem = new JMenuItem("Выход");

        logoutItem.addActionListener(e -> publisher.publishEvent(new LogoutEvent(MainFrame.this)));
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);
        return fileMenu;
    }

    public void setStatus(@NonNull String status) {
        toolbarLabel.setText(status);
    }

    public AudioTableModel getModel() {
        return model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case ACTION_SYNC:
                if (syncListener != null) {
                    syncListener.onSync();
                }
                break;
            case ACTION_SYNC_FAILED:
                if (syncListener != null) {
                    syncListener.onSyncFailed();
                }
                break;
            case ACTION_CHANGE_DESTINATION:
                if (destinationListener != null) {
                    destinationListener.chooseDestination();
                }
                break;
            default:
                throw new IllegalArgumentException("Unimplemented action command: " + e.getActionCommand());
        }
    }

    public void setAutoSync(boolean enabled) {
        autoSyncItem.setState(enabled);
        startSyncItem.setEnabled(!enabled);
        syncMenu.updateUI();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        boolean state = e.getStateChange() == ItemEvent.SELECTED;
        setAutoSync(state);

        if (syncListener != null) {
            syncListener.updateAutoSyncState(state);
        }
    }

    public interface OnSyncListener {
        void onSync();

        void onSyncFailed();

        void updateAutoSyncState(boolean enabled);
    }

    public interface OnChangeDestinationListener {
        void chooseDestination();
    }

}
