package me.ruslanys.vkmusic.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import me.ruslanys.vkmusic.annotation.FxmlController;

@FxmlController(view = "views/fxml/login.fxml")
public class LoginController {

    @FXML private WebView webView;
    @FXML private Pane loadingPane;

    @FXML
    public void init() {
        System.out.println("Hello from login");
    }

}
