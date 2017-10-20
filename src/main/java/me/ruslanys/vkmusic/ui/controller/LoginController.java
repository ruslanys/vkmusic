package me.ruslanys.vkmusic.ui.controller;

import com.google.common.collect.ImmutableMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import lombok.NonNull;
import lombok.SneakyThrows;
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
import java.net.CookieManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class LoginController implements CommandLineRunner, Runnable, ChangeListener<Worker.State> {

    private static final String SESSION_ID_KEY = "remixsid";
    private static final String LOGIN_PATH = "https://vk.com/login";
    private static final URI COOKIE_DOMAIN_URI = URI.create("https://vk.com/");

    private final LoginFrame loginFrame;

    private final VkClient vkClient;
    private final PropertyService propertyService;
    private final ScheduledExecutorService executor;

    private final MainController mainController;

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
    }

    @PostConstruct
    private void init() {
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
            loginFrame.setState(LoadingFrame.State.LOADING);
            loginFrame.setVisible(true);
            loginFrame.load(LOGIN_PATH);
        } else {
            executor.submit(() -> setSessionId(cookies.getSessionId()));
        }
    }

    public void setSessionId(@NonNull String sessionId) {
        try {
            vkClient.setCookies(ImmutableMap.of(SESSION_ID_KEY, sessionId));

            // check that session ID is good
            vkClient.fetchUserId();

            VkCookies cookies = new VkCookies(sessionId);
            propertyService.set(cookies);

            EventQueue.invokeLater(this::onAuthSuccess);
        } catch (Exception e) {
            onAuthFailed();
        }
    }

    private void onAuthSuccess() {
        loginFrame.setVisible(false);
        loginFrame.clear();
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

    /**
     * <p>It isn't possible to follow
     * <a href="https://docs.oracle.com/javase/tutorial/deployment/doingMoreWithRIA/accessingCookies.html">Oracle Accessing Cookies Tutorial</a>
     * via default {@link CookieManager} with {@link java.net.InMemoryCookieStore} under the hood.
     *
     * <p>{@link CookieManager} is the implementation of <a href="http://www.ietf.org/rfc/rfc2965.txt">RFC 2965</a>.
     * But the website needs to implement <a href="http://www.ietf.org/rfc/rfc6265.txt">RFC 6265</a>.
     *
     * <p>RFC 2965: "x.y.com domain-matches .Y.com but not Y.com."<br/>
     * RFC 6265: "The domain string is a suffix of the string. The last character of the string that is not included in the domain string is a "." character." <br/>
     * Take a look at <a href="https://github.com/square/okhttp/issues/991">OkHttp #991</a>.
     *
     * <p>So, private {@link com.sun.webkit.network.CookieManager} is implementing RFC 6265, that's why it's using.
     *
     * @return session id if it exists or {@code null}
     */
    @SneakyThrows
    private String fetchSessionId() {
        Map<String, List<String>> headers = CookieManager.getDefault().get(COOKIE_DOMAIN_URI, new HashMap<>());
        List<String> values = headers.getOrDefault("Cookie", new ArrayList<>());
        if (values.isEmpty()) {
            return null;
        }

        String headerValue = values.get(0);
        String[] cookieEntries = headerValue.split(";");
        for (String cookieEntry : cookieEntries) {
            String[] cookieParts = cookieEntry.split("=");
            if ("remixsid".equals(cookieParts[0].trim())) {
                return cookieParts[1].trim();
            }
        }

        return null;
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (newValue == Worker.State.SUCCEEDED) {
            String sessionId = fetchSessionId();
            if (sessionId != null) {
                setSessionId(sessionId);
            }
        }
    }
}
