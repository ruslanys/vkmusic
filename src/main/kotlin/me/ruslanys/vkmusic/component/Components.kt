package me.ruslanys.vkmusic.component

import me.ruslanys.vkmusic.entity.Audio
import java.io.File

interface PlaylistCreator {

    fun create(filename: String, audios: Collection<Audio>)

}

@Deprecated("Needs to be rewritten")
interface Id3vTagManager {

    @Deprecated("Needs to be rewritten")
    fun fix(file: File): File

}

interface VkClient {

    fun setCookies(cookies: Map<String, String>)

    fun addCookies(cookies: Map<String, String>)

    fun clearCookies()

    fun getAudio(): List<Audio>

    fun getAudio(ownerId: Long): List<Audio>

    fun fetchUserId(): Long

    fun fetchUrls(audioList: List<Audio>)

}