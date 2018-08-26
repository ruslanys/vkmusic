package me.ruslanys.vkmusic.exception

import me.ruslanys.vkmusic.entity.Audio

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
class DownloadException(
        cause: Throwable,
        val audio: Audio) : Exception(cause)
