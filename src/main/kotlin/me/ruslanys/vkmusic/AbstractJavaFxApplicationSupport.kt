package me.ruslanys.vkmusic

import javafx.application.Application
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

abstract class AbstractJavaFxApplicationSupport : Application() {

    protected lateinit var context: ConfigurableApplicationContext

    override fun init() {
        context = runApplication<me.ruslanys.vkmusic.Application>(/* args */) {
            setHeadless(false)
        }.also {
            it.autowireCapableBeanFactory.autowireBean(this)
        }
    }

    override fun stop() {
        context.stop()
    }

}