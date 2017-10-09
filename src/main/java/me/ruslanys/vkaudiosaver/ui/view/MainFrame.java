package me.ruslanys.vkaudiosaver.ui.view;

import lombok.NonNull;
import me.ruslanys.vkaudiosaver.entity.domain.event.LogoutEvent;
import me.ruslanys.vkaudiosaver.ui.model.AudioTableModel;
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
        JPanel panel = new JPanel(new BorderLayout(0, 0));

        final JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);

        JTable table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        model = new AudioTableModel();
        table.setModel(model);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

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

        JMenuItem logoutMI = new JMenuItem("Сменить пользователя");
        logoutMI.addActionListener(e -> publisher.publishEvent(new LogoutEvent(MainFrame.this)));
        JMenuItem exitMI = new JMenuItem("Выход");
        exitMI.addActionListener(e -> System.exit(0));

        fileMenu.add(logoutMI);
        fileMenu.add(exitMI);

        // settings
        JMenu settingsMenu = new JMenu("Настройки");

        JMenuItem destinationMI = new JMenuItem("Указать папку");
        settingsMenu.add(destinationMI);

        // --
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);

        setJMenuBar(menuBar);
    }

    public void setStatus(@NonNull String status) {
        toolbarLabel.setText(status);
    }

    public AudioTableModel getModel() {
        return model;
    }

}
