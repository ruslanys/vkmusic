package me.ruslanys.vkmusic.ui.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkmusic.component.VkClient;
import me.ruslanys.vkmusic.entity.domain.event.LogoutEvent;
import me.ruslanys.vkmusic.property.VkProperties;
import me.ruslanys.vkmusic.services.PropertyService;
import me.ruslanys.vkmusic.ui.view.LoginFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class StartController implements CommandLineRunner, Runnable, LoginFrame.OnSubmitListener {

    private final LoginFrame loginFrame;

    private final VkClient vkClient;
    private final PropertyService propertyService;
    private final ScheduledExecutorService executor;

    private final MainController mainController;

    @Autowired
    public StartController(@NonNull LoginFrame loginFrame,
                           @NonNull VkClient vkClient,
                           @NonNull PropertyService propertyService,
                           @NonNull ScheduledExecutorService executor,
                           @NonNull MainController mainController) {
        this.loginFrame = loginFrame;
        this.propertyService = propertyService;
        this.vkClient = vkClient;
        this.executor = executor;
        this.mainController = mainController;
    }

    @PostConstruct
    private void init() {
        loginFrame.setSubmitListener(this);
    }

    @Override
    public void run(String... args) throws Exception {
        EventQueue.invokeLater(this);
    }

    @Override
    public void run() {
        VkProperties vkProperties = propertyService.get(VkProperties.class);
        if (vkProperties == null) {
            showLoginForm(LoginFrame.State.MAIN);
        } else {
            showLoginForm(LoginFrame.State.LOADING);
            executor.submit(() -> onSubmit(vkProperties.getUsername(), vkProperties.getPassword()));
        }
    }

    @Override
    public void onSubmit(@NonNull String username, @NonNull String password) {
        try {
            VkProperties properties = new VkProperties(username, password);
            vkClient.auth(properties);
            propertyService.set(properties);

            EventQueue.invokeLater(this::onAuthSuccess);
        } catch (Exception e) {
            onAuthFailed();
        }
    }

    private void onAuthSuccess() {
        loginFrame.dispose();
        mainController.run();
    }

    private void onAuthFailed() {
        vkClient.clear();
        propertyService.remove(VkProperties.class);
        showLoginForm(LoginFrame.State.MAIN);
    }

    @EventListener
    public void logout(@NonNull LogoutEvent event) {
        event.getSource().dispose();
        onAuthFailed();
    }

    private void showLoginForm(LoginFrame.State state) {
        loginFrame.setState(state);
        loginFrame.setVisible(true);
    }
}
