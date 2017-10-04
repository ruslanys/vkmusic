package me.ruslanys.vkaudiosaver.ui.controller;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.component.VkClient;
import me.ruslanys.vkaudiosaver.domain.event.LogoutEvent;
import me.ruslanys.vkaudiosaver.property.VkProperties;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import me.ruslanys.vkaudiosaver.ui.view.LoginFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.EventListener;
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
public class StartController implements CommandLineRunner, Runnable {

    private final LoginFrame loginFrame;

    private final PropertyService propertyService;
    private final VkClient vkClient;

    private final MainController mainController;

    @Autowired
    public StartController(@NonNull LoginFrame loginFrame,
                           @NonNull PropertyService propertyService,
                           @NonNull VkClient vkClient,
                           @NonNull MainController mainController) {
        this.loginFrame = loginFrame;
        this.propertyService = propertyService;
        this.vkClient = vkClient;
        this.mainController = mainController;
    }

    @PostConstruct
    private void init() {
        loginFrame.setSubmitListener(this::auth);
    }

    @Override
    public void run(String... args) throws Exception {
        EventQueue.invokeLater(this);
        displayTray();
    }

    @Override
    public void run() {
        VkProperties vkProperties = propertyService.getVkProperties();
        if (vkProperties == null) {
            showLoginForm(LoginFrame.State.LOGIN);
        } else {
            showLoginForm(LoginFrame.State.LOADING);
            Executors.newSingleThreadExecutor()
                    .submit(() -> auth(vkProperties.getUsername(), vkProperties.getPassword()));
        }
    }

    private void auth(@NonNull final String username, @NonNull final String password) {
        try {
            VkProperties properties = new VkProperties(username, password);
            vkClient.auth(properties);
            propertyService.save(properties);

            onAuthSuccess();
        } catch (Exception e) {
            onAuthFailed();
        }
    }

    private void onAuthSuccess() {
        loginFrame.setVisible(false);
        mainController.run();
    }

    private void onAuthFailed() {
        propertyService.cleanVkProperties();
        showLoginForm(LoginFrame.State.LOGIN);
    }

    @EventListener
    public void logout(LogoutEvent event) {
        event.getSource().setVisible(false);
        onAuthFailed();
    }

    private void showLoginForm(LoginFrame.State state) {
        loginFrame.setState(state);
        loginFrame.setVisible(true);
    }

    @SneakyThrows
    private void displayTray() {
        SystemTray tray = SystemTray.getSystemTray();

//        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getClassLoader().getResource("images/icon.png"));
        BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource("images/icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, "VkMusic");
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(event -> mainController.run());

        tray.add(trayIcon);
    }

}
