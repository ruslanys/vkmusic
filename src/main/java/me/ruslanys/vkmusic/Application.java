package me.ruslanys.vkmusic;

import javafx.scene.Scene;
import javafx.stage.Stage;
import me.ruslanys.vkmusic.ui.controller.LoginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application extends AbstractJavaFxApplicationSupport {

    private LoginController loginController;

    @Autowired
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Авторизация");

        stage.setScene(new Scene(loginController.getRootView()));

        stage.setMinWidth(640);
        stage.setMinHeight(480);

        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launchApp(Application.class, args);
    }

}
