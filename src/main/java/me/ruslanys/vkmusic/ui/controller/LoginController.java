package me.ruslanys.vkmusic.ui.controller;

import com.google.common.collect.ImmutableMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkmusic.component.VkClient;
import me.ruslanys.vkmusic.entity.domain.event.LogoutEvent;
import me.ruslanys.vkmusic.property.VkCookies;
import me.ruslanys.vkmusic.services.PropertyService;
import me.ruslanys.vkmusic.ui.view.LoadingFrame;
import me.ruslanys.vkmusic.ui.view.LoginFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class LoginController implements CommandLineRunner, Runnable, ChangeListener<Worker.State> {

    private static final String SESSION_ID_KEY = "remixsid";

    private final LoginFrame loginFrame;

    private final VkClient vkClient;
    private final PropertyService propertyService;
    private final ScheduledExecutorService executor;

    private final MainController mainController;
    private final CookieManager cookieManager;

    @Autowired
    public LoginController(@NonNull LoginFrame loginFrame,
                           @NonNull VkClient vkClient,
                           @NonNull PropertyService propertyService,
                           @NonNull ScheduledExecutorService executor,
                           @NonNull MainController mainController) {
        this.loginFrame = loginFrame;
        this.propertyService = propertyService;
        this.vkClient = vkClient;
        this.executor = executor;
        this.mainController = mainController;

        this.cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    }

    @PostConstruct
    private void init() {
        CookieHandler.setDefault(cookieManager);
        loginFrame.addChangeListener(this);
    }

    @Override
    public void run(String... args) throws Exception {
        EventQueue.invokeLater(this);
    }

    @Override
    public void run() {
        VkCookies cookies = propertyService.get(VkCookies.class);
        if (cookies == null) {
            loginFrame.load("https://vk.com");
            loginFrame.setVisible(true);
        } else {
            loginFrame.setState(LoadingFrame.State.LOADING);
            executor.submit(() -> setSessionId(cookies.getSessionId()));
        }
    }

    public void setSessionId(@NonNull String sessionId) {
        try {
            vkClient.setCookies(ImmutableMap.of(SESSION_ID_KEY, sessionId));
            vkClient.fetchUserId();

            VkCookies cookies = new VkCookies(sessionId);
            propertyService.set(cookies);

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
        propertyService.remove(VkCookies.class);
        run();
    }

    @EventListener
    public void logout(@NonNull LogoutEvent event) {
        event.getSource().dispose();
        onAuthFailed();
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (newValue == Worker.State.SUCCEEDED) {
            List<HttpCookie> cookies = cookieManager.getCookieStore().get(URI.create("https://vk.com"));
            for (HttpCookie cookie : cookies) {
                if (SESSION_ID_KEY.equals(cookie.getName())) {
                    setSessionId(cookie.getValue());
                    break;
                }
            }

        }
    }
}
