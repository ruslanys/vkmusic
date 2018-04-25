package me.ruslanys.vkmusic;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application extends AbstractJavaFxApplicationSupport {

    @Value("${controller.title:JavaFX приложение}")//
    private String windowTitle;

    @Qualifier("mainView")
    @Autowired
    private Parent view;

    @Override
    public void start(Stage stage) {
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(view));
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launchApp(Application.class, args);
    }

}
