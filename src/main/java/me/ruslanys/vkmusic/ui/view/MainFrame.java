package me.ruslanys.vkmusic.ui.view;

import lombok.NonNull;
import me.ruslanys.vkmusic.entity.domain.event.LogoutEvent;
import me.ruslanys.vkmusic.ui.model.AudioTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class MainFrame extends LoadingFrame {

    private final ApplicationEventPublisher publisher;

    private AudioTableModel model;
    private AboutFrame aboutFrame;

    private JLabel toolbarLabel;

    @Autowired
    public MainFrame(ApplicationEventPublisher publisher) throws Exception {
        super();
        this.publisher = publisher;

        initMenu();
    }

    @Override
    protected void initWindow() {
        setTitle("VKMusic");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(550, 300);
        setLocationRelativeTo(null);
    }

    @Override
    protected JPanel initMainPanel() {
        model = new AudioTableModel();
        aboutFrame = new AboutFrame();

        JPanel panel = new JPanel(new BorderLayout(0, 0));

        final JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);

        JTable table = new JTable() {
            @Override
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        table.setModel(model);

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

        // file
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem logoutItem = new JMenuItem("Сменить пользователя");
        logoutItem.addActionListener(e -> publisher.publishEvent(new LogoutEvent(MainFrame.this)));
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);

        // settings
        JMenu settingsMenu = new JMenu("Настройки");

        JMenuItem destinationItem = new JMenuItem("Указать папку");
        settingsMenu.add(destinationItem);

        // Help
        JMenu helpMenu = new JMenu("Помощь");

        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> aboutFrame.setVisible(true));
        helpMenu.add(aboutItem);

        // --
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    public void setStatus(@NonNull String status) {
        toolbarLabel.setText(status);
    }

    public AudioTableModel getModel() {
        return model;
    }

}
