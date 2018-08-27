package me.ruslanys.vkmusic.service

import me.ruslanys.vkmusic.component.VkClient
import me.ruslanys.vkmusic.domain.Audio
import me.ruslanys.vkmusic.event.DownloadFailEvent
import me.ruslanys.vkmusic.event.DownloadSuccessEvent
import me.ruslanys.vkmusic.property.DownloadProperties
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class DefaultDownloadService(
        propertyService: PropertyService,
        private val vkClient: VkClient,
        private val publisher: ApplicationEventPublisher
) : DownloadService {

    private val executor: ExecutorService

    init {
        val downloadProperties = propertyService.get(DownloadProperties::class.java)
        executor = Executors.newFixedThreadPool(downloadProperties!!.poolSize)
    }

    override fun download(destination: String, audio: Audio) {
        download(destination, listOf(audio))
    }

    override fun download(destination: String, audioList: List<Audio>) {
        log.info("Download [{}]", audioList.joinToString { it.id.toString() })
        vkClient.fetchUrls(audioList)

        val destinationFolder = File(destination)
        if (destinationFolder.exists() && !destinationFolder.isDirectory) {
            throw IllegalArgumentException("Incorrect destination path.")
        } else if (!destinationFolder.mkdirs()) {
            throw IllegalStateException("Can not create destination folder.")
        }

        audioList.forEach {
            executor.submit {
                downloadFile(destinationFolder, it)
            }
        }
    }

    private fun downloadFile(destinationFolder: File, audio: Audio) {
        log.info("Download file {}", audio.url)

        try {
            val connection = URL(audio.url).openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val file = File(destinationFolder, audio.filename())
            BufferedInputStream(connection.inputStream, 5120).use { input ->
                BufferedOutputStream(FileOutputStream(file), 5120).use { output ->
                    val buff = ByteArray(5120)
                    var len: Int

                    do {
                        len = input.read(buff)
                        if (len > 0) output.write(buff, 0, len)
                    } while (len != -1)
                }
            }

            publisher.publishEvent(DownloadSuccessEvent(this, audio, file))
        } catch (e: Exception) {
            publisher.publishEvent(DownloadFailEvent(this, audio))
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultDownloadService::class.java)
    }

}

fun Audio.filename(): String {
    val regex = "[!\"#$%&'()*+,\\-/:;<=>?@\\[\\]^_`{|}~]".toRegex()
    val formattedArtist = artist.trim().replace(regex, "")
    val formattedTitle = title.trim().replace(regex, "")

    return "${formattedArtist.substring(0..15)} - ${formattedTitle.substring(0..20)}.mp3"
}