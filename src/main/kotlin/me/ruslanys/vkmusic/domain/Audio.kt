package me.ruslanys.vkmusic.domain

data class Audio(
        var id: Long,
        var ownerId: Long,
        var artist: String,
        var title: String,
        var duration: Int,
        var status: DownloadStatus = DownloadStatus.NEW,
        var url: String? = null
)