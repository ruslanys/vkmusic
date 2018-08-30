package me.ruslanys.vkmusic.event

import me.ruslanys.vkmusic.domain.Audio
import org.springframework.context.ApplicationEvent

abstract class DownloadEvent(
        source: Any,
        val audio: Audio
) : ApplicationEvent(source)
