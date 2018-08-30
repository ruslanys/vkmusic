package me.ruslanys.vkmusic.event

import me.ruslanys.vkmusic.domain.Audio

class DownloadFailEvent(
        source: Any,
        audio: Audio,
        val cause: Throwable
) : DownloadEvent(source, audio)
