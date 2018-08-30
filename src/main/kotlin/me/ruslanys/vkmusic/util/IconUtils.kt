package me.ruslanys.vkmusic.util

import javafx.scene.image.Image
import me.ruslanys.vkmusic.domain.DownloadStatus
import java.util.*

object IconUtils {

    private val desktopIcon: Image
    private val loadingIcon: Image
    private val statusIcon: Map<DownloadStatus, Image>

    init {
        desktopIcon = loadImage("images/icon.png")
        loadingIcon = loadImage("images/loading-heart.gif")
        statusIcon = EnumMap<DownloadStatus, Image>(DownloadStatus::class.java)
        for (status in DownloadStatus.values()) {
            val imageView = loadImage("images/status/${status.name.toLowerCase()}.png")
            statusIcon[status] = imageView
        }
    }

    fun getLoadingIcon(): Image = loadingIcon

    fun getStatusIcon(status: DownloadStatus): Image = statusIcon[status]!!

    fun getDesktopIcon(): Image = desktopIcon

    private fun loadImage(path: String): Image {
        return Image(IconUtils::class.java.classLoader.getResourceAsStream(path))
    }

}