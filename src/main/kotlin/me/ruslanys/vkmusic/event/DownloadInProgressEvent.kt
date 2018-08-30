package me.ruslanys.vkmusic.event

import me.ruslanys.vkmusic.domain.Audio

class DownloadInProgressEvent(
        source: Any,
        audio: Audio
) : DownloadEvent(source, audio)
