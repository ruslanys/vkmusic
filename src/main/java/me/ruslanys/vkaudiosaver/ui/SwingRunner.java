package me.ruslanys.vkaudiosaver.ui;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.util.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class SwingRunner implements CommandLineRunner {

    private final CredentialsFrame credentialsFrame;
    private final MainFrame mainFrame;

    @Autowired
    public SwingRunner(CredentialsFrame credentialsFrame, MainFrame mainFrame) {
        this.credentialsFrame = credentialsFrame;
        this.mainFrame = mainFrame;
    }

    @Override
    public void run(String... args) throws Exception {
        java.awt.EventQueue.invokeLater(() -> {
            displayTray();

            mainFrame.setVisible(true);
        });
    }

    @SneakyThrows
    private void displayTray() {
        SystemTray tray = SystemTray.getSystemTray();

//        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getClassLoader().getResource("images/icon.png"));
        BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource("images/icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, "VkMusic");
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener((event) -> {
            Notifications.ubuntuHello();
            Notifications.windowsHello();


            mainFrame.setVisible(true);
        });

        tray.add(trayIcon);
    }


}
