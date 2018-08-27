package me.ruslanys.vkmusic.util

import me.ruslanys.vkmusic.Application
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.awt.Image
import java.io.IOException
import java.net.URI
import java.net.URL
import javax.imageio.ImageIO

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
object DesktopUtils {

    private val log = LoggerFactory.getLogger(DesktopUtils::class.java)

    val icon: Image?
    private val DESKTOP = Desktop.getDesktop()

    init {
        val image: Image? = try {
            val resource = Application::class.java.classLoader.getResource("images/icon.png")
            ImageIO.read(resource!!)
        } catch (e: IOException) {
            log.error("Can not load an application icon", e)
            null
        }

        icon = image
    }

    fun browse(uri: URI) {
        DESKTOP.browse(uri)
    }

    fun browse(url: URL) {
        browse(url.toURI())
    }

}
