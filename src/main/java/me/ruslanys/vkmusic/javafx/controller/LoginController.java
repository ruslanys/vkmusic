package me.ruslanys.vkmusic.javafx.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.ruslanys.vkmusic.annotation.FxmlController;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FxmlController(view = "views/fxml/login.fxml")
public class LoginController implements ChangeListener<Worker.State> {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36";

    private static final String SESSION_ID_KEY = "remixsid";
    private static final String LOGIN_PATH = "https://vk.com/login";
    private static final URI COOKIE_DOMAIN_URI = URI.create("https://vk.com/");

    @FXML private Pane loadingView;
    @FXML private BorderPane loadingFrame;
    @FXML private ImageView loadingImageView;

    private WebView webView;

    @FXML
    public void initialize() {
        // Этап инициализации JavaFX
        System.out.println("HELLO");

        // WebView
        Platform.runLater(() -> {
            webView = new WebView();
            WebEngine engine = webView.getEngine();
            engine.setJavaScriptEnabled(true);
            engine.setUserAgent(USER_AGENT);

            webView.getEngine().getLoadWorker().stateProperty().addListener(this);

            webView.setVisible(false);
            loadingView.getChildren().add(webView);

            webView.getEngine().load(LOGIN_PATH);
        });

        // Image Icon
        InputStream resource = getClass().getClassLoader().getResourceAsStream("images/loading-heart.gif");
        Image image = new Image(resource);

        loadingImageView.setImage(image);



//        loadingView.
//        loadingFrame,.setScene(new Scene(webView));
    }

    @PostConstruct
    private void init() {
        System.out.println("s");
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
        switch (newValue) {
            case SUCCEEDED:
                webView.setVisible(true);
                loadingFrame.setVisible(false);

                String sessionId = fetchSessionId();
                if (sessionId != null) {
                    setSessionId(sessionId);
                }
                break;
            case SCHEDULED:
                webView.setVisible(false);
                loadingFrame.setVisible(true);
                break;
        }
    }

    public void setSessionId(@NonNull String sessionId) {
//        try {
//            vkClient.setCookies(ImmutableMap.of(SESSION_ID_KEY, sessionId));
//
//            // check that session ID is good
//            vkClient.fetchUserId();
//
//            VkCookies cookies = new VkCookies(sessionId);
//            propertyService.set(cookies);
//
//            EventQueue.invokeLater(this::onAuthSuccess);
//        } catch (Exception e) {
//            onAuthFailed();
//        }
    }

}
