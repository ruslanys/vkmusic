package me.ruslanys.vkaudiosaver.ui;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.component.VkClient;
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
import java.util.concurrent.Executors;

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
        EventQueue.invokeLater(this);
    }

    @Override
    public void run() {
        VkProperties vkProperties = propertyService.getVkProperties();
        if (vkProperties == null) {
            showLoginForm(LoginFrame.State.LOGIN);
        } else {
            auth(vkProperties.getUsername(), vkProperties.getPassword());
        }
    }

    private void auth(final String username, final String password) {
        showLoginForm(LoginFrame.State.LOADING);

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                VkProperties properties = new VkProperties(username, password);
                vkClient.auth(properties);
                propertyService.save(properties);

                showMainForm();
                displayTray();
            } catch (Exception e) {
                propertyService.cleanVkProperties();
                showLoginForm(LoginFrame.State.LOGIN);
            }
        });
    }

    private void showLoginForm(LoginFrame.State state) {
        mainFrame.setVisible(false);
        loginFrame.setState(state);
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
