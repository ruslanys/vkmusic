package me.ruslanys.vkmusic.domain

import javafx.scene.image.ImageView
import me.ruslanys.vkmusic.util.IconUtils

data class Audio(
        val id: Long,
        val ownerId: Long,
        val artist: String,
        val title: String,
        val durationInSec: Int,
        var status: DownloadStatus = DownloadStatus.NEW,
        var url: String? = null
) {

    fun getStatusIcon(): ImageView {
        val view = ImageView(IconUtils.getStatusIcon(status))
        view.isPreserveRatio = true
        view.fitHeight = 20.0
        view.fitWidth = 20.0
        return view
    }

    fun getDuration(): String {
        val minutes = durationInSec / 60
        val seconds = durationInSec % 60

        val minutesStr = minutes.toString()
        val secondStr = if (seconds < 10) "0$seconds" else seconds.toString()

        return "$minutesStr:$secondStr"
    }

}