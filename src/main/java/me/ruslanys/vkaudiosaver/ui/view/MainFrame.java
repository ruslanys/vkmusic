package me.ruslanys.vkaudiosaver.ui.view;

import me.ruslanys.vkaudiosaver.domain.event.LogoutEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class MainFrame extends JFrame {

    private final ApplicationEventPublisher publisher;

    private JTable table;
    private JLabel toolbarLabel;

    @Autowired
    public MainFrame(ApplicationEventPublisher publisher) {
        this.publisher = publisher;

        initComponents();
        initMenu();
    }

    private void initComponents() {
        setTitle("VKMusic");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // ui components
        getContentPane().setLayout(new BorderLayout(0, 0));

        final JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        table = new JTable();
        scrollPane.setViewportView(table);

        final JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMargin(new Insets(0, 5, 0, 0));
        getContentPane().add(toolBar, BorderLayout.SOUTH);

        toolbarLabel = new JLabel("...");
        toolBar.add(toolbarLabel);
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

    public void setStatus(String status) {
        toolbarLabel.setText(status);
    }

}
