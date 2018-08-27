package me.ruslanys.vkmusic.component

import com.mpatric.mp3agic.Mp3File
import com.mpatric.mp3agic.NotSupportedException

import java.io.File
import java.nio.charset.Charset

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
//@Component
class DefaultId3vTagManager : Id3vTagManager {

    override fun fix(source: File): File {
        val mp3File = Mp3File(source)

        mp3File.removeCustomTag()

        if (mp3File.hasId3v1Tag()) {
            val tag = mp3File.id3v1Tag

            tag.artist = fixCharset(tag.artist)
            tag.title = fixCharset(tag.title)
            tag.album = fixCharset(tag.album)
            tag.comment = fixCharset(tag.comment)

            mp3File.id3v1Tag = tag
        }

        if (mp3File.hasId3v2Tag()) {
            val tag = mp3File.id3v2Tag

            tag.artist = fixCharset(tag.artist)
            tag.title = fixCharset(tag.title)
            tag.album = fixCharset(tag.album)
            tag.comment = fixCharset(tag.comment)

            mp3File.id3v2Tag = tag
        }

        val filename = source.toString() + "_fixed.mp3"
        try {
            mp3File.save(filename)
        } catch (e: NotSupportedException) {
            mp3File.removeId3v2Tag()
            mp3File.save(filename)
        }

        return File(filename)
    }

    private fun fixCharset(string: String): String =
            if (string == "") string
            else String(string.toByteArray(Charsets.ISO_8859_1), Charset.forName("windows-1251"))

}
