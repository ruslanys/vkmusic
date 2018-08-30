package me.ruslanys.vkmusic.service

import me.ruslanys.vkmusic.domain.Audio
import me.ruslanys.vkmusic.property.Properties
import java.io.File

interface PropertyService {

    fun <T : Properties> set(properties: T): T

    fun <T : Properties> get(clazz: Class<T>): T?

    fun <T : Properties> remove(clazz: Class<T>)

}

interface DownloadService {

    fun download(destination: File, audio: Audio)

    fun download(destination: File, audioList: List<Audio>)

}
