package me.ruslanys.vkmusic.ui.view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import me.ruslanys.vkmusic.util.DesktopUtils;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.CookieManager;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class LoginFrame extends LoadingFrame implements ChangeListener<Worker.State> {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36";

    private WebView webView;


    @Override
    protected void initWindow() {
        setTitle("Авторизация");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(DesktopUtils.getIcon());

        setSize(1000, 660);
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
    }

    @Override
    protected JComponent initMainPanel() {
        JFXPanel panel = new JFXPanel();
        Platform.runLater(() -> {
            webView = new WebView();
            WebEngine engine = webView.getEngine();
            engine.setJavaScriptEnabled(true);
            engine.setUserAgent(USER_AGENT);

            addChangeListener(LoginFrame.this);
            panel.setScene(new Scene(webView));
        });
        return panel;
    }

    public void addChangeListener(ChangeListener<Worker.State> changeListener) {
        Platform.runLater(() -> webView.getEngine().getLoadWorker().stateProperty().addListener(changeListener));
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
        switch (newState) {
            case SUCCEEDED:
                setState(State.MAIN);
                break;
            case SCHEDULED:
                setState(State.LOADING);
                break;
        }
    }

    public void load(String url) {
        Platform.runLater(() -> webView.getEngine().load(url));
    }

    public void clear() {
        CookieManager.setDefault(new com.sun.webkit.network.CookieManager());
        Platform.runLater(() -> webView.getEngine().loadContent(""));
    }

}
