package me.ruslanys.vkaudiosaver.ui;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.component.VkClient;
import me.ruslanys.vkaudiosaver.exception.VkException;
import me.ruslanys.vkaudiosaver.property.VkProperties;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import me.ruslanys.vkaudiosaver.util.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class MainController implements CommandLineRunner, Runnable {

    private final LoginFrame loginFrame;
    private final MainFrame mainFrame;

    private final PropertyService propertyService;
    private final VkClient vkClient;

    @Autowired
    public MainController(@NonNull LoginFrame loginFrame,
                          @NonNull MainFrame mainFrame,
                          @NonNull PropertyService propertyService,
                          @NonNull VkClient vkClient) {
        this.loginFrame = loginFrame;
        this.mainFrame = mainFrame;
        this.propertyService = propertyService;
        this.vkClient = vkClient;
    }

    @PostConstruct
    private void init() {
        loginFrame.setSubmitListener(this::auth);
    }

    @Override
    public void run(String... args) throws Exception {
        java.awt.EventQueue.invokeLater(this);
    }

    @Override
    public void run() {
        VkProperties vkProperties = propertyService.getVkProperties();
        if (vkProperties == null) {
            showLoginForm();
        } else {
            auth(vkProperties.getUsername(), vkProperties.getPassword());
            showMainForm();
            displayTray();
        }
    }

    private void auth(String username, String password) {
        try {
            VkProperties properties = new VkProperties(username, password);
            vkClient.auth(properties);
            propertyService.save(properties);
        } catch (VkException e) {
            propertyService.cleanVkProperties();
            showLoginForm();
        }
    }

    private void showLoginForm() {
        mainFrame.setVisible(false);
        loginFrame.setVisible(true);
    }

    private void showMainForm() {
        loginFrame.setVisible(false);
        mainFrame.setVisible(true);
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
