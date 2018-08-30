package me.ruslanys.vkmusic.service

import me.ruslanys.vkmusic.domain.Audio
import me.ruslanys.vkmusic.property.Properties

interface PropertyService {

    fun <T : Properties> set(properties: T): T

    fun <T : Properties> get(clazz: Class<T>): T?

    fun <T : Properties> remove(clazz: Class<T>)

}

interface DownloadService {

    fun download(destination: String, audio: Audio)

    fun download(destination: String, audioList: List<Audio>)

}
