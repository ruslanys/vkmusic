package me.ruslanys.vkmusic.util

import javafx.scene.image.Image

object IconUtils {

    fun getLoadingIcon(): Image {
        val resource = IconUtils::class.java.classLoader.getResourceAsStream("images/loading-heart.gif")
        return Image(resource)
    }

    fun getDesktopIcon(): Image {
        val resource = IconUtils::class.java.classLoader.getResourceAsStream("images/icon.png")
        return Image(resource)
    }

}