package me.ruslanys.vkmusic;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application extends AbstractJavaFxApplicationSupport {

//    @Qualifier("loginView")
//    @Autowired
//    private Parent view;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Авторизация");

        stage.setScene(new Scene(new StackPane()));

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
