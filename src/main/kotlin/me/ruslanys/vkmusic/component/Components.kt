package me.ruslanys.vkmusic.component

import me.ruslanys.vkmusic.entity.Audio

interface PlaylistCreator {

    fun create(filename: String, audios: Collection<Audio>)

}