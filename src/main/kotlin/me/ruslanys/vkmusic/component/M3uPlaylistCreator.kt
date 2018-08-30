package me.ruslanys.vkmusic.component

import me.ruslanys.vkmusic.domain.Audio
import me.ruslanys.vkmusic.service.filename
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
class M3uPlaylistCreator : PlaylistCreator {

    override fun create(filename: String, audios: Collection<Audio>) {
        try {
            BufferedWriter(FileWriter(filename)).use { writer ->
                writer.write("#EXTM3U")
                writer.newLine()
                writer.newLine()

                for (audio in audios) {
                    writer.write(String.format("#EXTINF:%d, %s - %s", audio.durationInSec, audio.artist, audio.title))
                    writer.newLine()

                    writer.write(audio.filename())
                    writer.newLine()
                    writer.newLine()
                }
            }
        } catch (e: IOException) {
            log.error("Exception due playlist creation process.", e)
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(M3uPlaylistCreator::class.java)
    }

}
