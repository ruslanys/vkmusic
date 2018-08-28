package me.ruslanys.vkmusic.util

import java.awt.Desktop
import java.net.URI
import java.net.URL

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
object DesktopUtils {

    private val DESKTOP = Desktop.getDesktop()

    fun browse(uri: URI) {
        DESKTOP.browse(uri)
    }

    fun browse(url: URL) {
        browse(url.toURI())
    }

}
