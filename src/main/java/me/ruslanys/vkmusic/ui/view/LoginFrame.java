package me.ruslanys.vkmusic.ui.view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class LoginFrame extends LoadingFrame implements ChangeListener<Worker.State> {

    private WebView webView;


    @Override
    protected void initWindow() {
        setSize(640, 480);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        setTitle("Авторизация");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    protected JComponent initMainPanel() {
        JFXPanel panel = new JFXPanel();
        Platform.runLater(() -> {
            webView = new WebView();
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
            case READY:
            case SUCCEEDED:
            case FAILED:
            case CANCELLED:
                setState(State.MAIN);
                break;
            case SCHEDULED:
            case RUNNING:
                setState(State.LOADING);
                break;
        }
    }

    public void load(String url) {
        Platform.runLater(() -> {
            setState(State.LOADING);
            webView.getEngine().load(url);
        });
    }

}
