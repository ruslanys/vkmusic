package me.ruslanys.vkmusic.event

import me.ruslanys.vkmusic.domain.Audio
import java.io.File

class DownloadSuccessEvent(
        source: Any,
        audio: Audio,
        val file: File
) : DownloadEvent(source, audio)
