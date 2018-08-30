package me.ruslanys.vkmusic

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import me.ruslanys.vkmusic.ui.controller.LoginController
import me.ruslanys.vkmusic.util.IconUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application : AbstractJavaFxApplicationSupport() {

    @set:Autowired
    lateinit var loginController: LoginController

    override fun start(stage: Stage) {
        stage.icons.add(IconUtils.getDesktopIcon())
        stage.title = "Авторизация"

        stage.scene = Scene(loginController.rootView!!)

        stage.minWidth = 640.0
        stage.minHeight = 480.0

        stage.isResizable = true
        stage.centerOnScreen()
        stage.show()
    }

}

fun main(args: Array<String>) {
    Application.launch(me.ruslanys.vkmusic.Application::class.java)
}