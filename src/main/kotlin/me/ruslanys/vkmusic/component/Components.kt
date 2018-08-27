package me.ruslanys.vkmusic.component

import me.ruslanys.vkmusic.entity.Audio
import java.io.File

interface PlaylistCreator {

    fun create(filename: String, audios: Collection<Audio>)

}

interface Id3vTagManager {

    @Deprecated("Needs to be rewritten")
    fun fix(file: File): File

}