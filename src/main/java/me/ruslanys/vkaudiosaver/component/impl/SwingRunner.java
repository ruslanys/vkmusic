package me.ruslanys.vkaudiosaver.component.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.ui.CredentialsFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.awt.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class SwingRunner implements CommandLineRunner {

    private final CredentialsFrame frame;

    @Autowired
    public SwingRunner(CredentialsFrame frame) {
        this.frame = frame;
    }

    @Override
    public void run(String... args) throws Exception {
        java.awt.EventQueue.invokeLater(() -> { displayTray(); frame.setVisible(true); });
    }

    @SneakyThrows
    private void displayTray() {
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getClassLoader().getResource("images/icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, "VkMusic");
        trayIcon.setImageAutoSize(true);

        tray.add(trayIcon);
    }

}
