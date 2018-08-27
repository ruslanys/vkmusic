package me.ruslanys.vkmusic.component

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.ruslanys.vkmusic.entity.Audio
import me.ruslanys.vkmusic.exception.VkException
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern
import javax.script.Invocable
import javax.script.ScriptEngineManager

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Suppress("JoinDeclarationAndAssignment")
@Component
class ScraperVkClient : VkClient {

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36"
        private const val PATH_BASE = "https://vk.com"
        private const val JSON_DELIMITER = "<!json>"
        private const val BLOCK_DELIMITER = "<!>"
        private const val SLEEP_INTERVAL = 5000L

        private val SCRIPT_ENGINE = ScriptEngineManager().getEngineByName("JavaScript")
        private val log = LoggerFactory.getLogger(ScraperVkClient::class.java)
    }

    private val script: String
    private val cookies = ConcurrentHashMap<String, String>()

    init {
        script = BufferedReader(InputStreamReader(javaClass.classLoader.getResourceAsStream("decrypt.js")))
                .use(BufferedReader::readText)
    }


    override fun addCookies(cookies: Map<String, String>) {
        this.cookies.putAll(cookies)
    }

    override fun setCookies(cookies: Map<String, String>) {
        clearCookies()
        addCookies(cookies)
    }

    override fun clearCookies() {
        this.cookies.clear()
    }

    override fun fetchUserId(): Long {
        val response = Jsoup.connect(PATH_BASE)
                .userAgent(USER_AGENT).cookies(cookies).method(Connection.Method.GET)
                .execute()
        val matcher = Pattern.compile("id: (\\d+)").matcher(response.body())
        if (!matcher.find() || "0" == matcher.group(1).trim()) {
            throw VkException("Не удалось получить ID пользователя.")
        }
        val id = matcher.group(1).toLong()
        log.info("User ID: {}", id)

        return id
    }

    override fun getAudio(): List<Audio> {
        val userId = fetchUserId()
        return getAudio(userId)
    }

    override fun getAudio(ownerId: Long): List<Audio> {
        val list = mutableListOf<Audio>()
        var offset = 0

        var audioDto: VkAudioDto
        do {
            audioDto = fetchAudioChunk(ownerId, offset)
            list.addAll(audioDto.audio)
            offset = audioDto.nextOffset
        } while (audioDto.hasMore())

        log.debug("Total count: {}", audioDto.totalCount)
        log.debug("Fetched audio collection: {}", list.size)
        return list
    }

    private fun fetchAudioChunk(ownerId: Long, offset: Int): VkAudioDto {
        val response = Jsoup.connect("$PATH_BASE/al_audio.php")
                .userAgent(USER_AGENT).cookies(cookies).method(Connection.Method.POST)
                .data("access_hash", "")
                .data("act", "load_section")
                .data("al", "1")
                .data("claim", "0")
                .data("offset", offset.toString())
                .data("owner_id", ownerId.toString())
                .data("playlist_id", "-1")
                .data("type", "playlist")
                .execute()

        val body = response.body()
        val trimmed = body.substring(body.indexOf(JSON_DELIMITER) + JSON_DELIMITER.length)
        val json = trimmed.substring(0..body.indexOf(BLOCK_DELIMITER))

        return jacksonObjectMapper().readValue(json, VkAudioDto::class.java)
    }

    override fun fetchUrls(audioList: List<Audio>) {
        val userId = fetchUserId()
        var sleepInterval = SLEEP_INTERVAL

        val chunks = audioList.chunked(10)
        var chunkNumber = 0
        while (chunkNumber < chunks.size) {
            val chunkContent = chunks[chunkNumber]

            log.debug("Fetching urls: ${chunkContent.size}")

            try {
                fetchUrlsChunk(userId, chunkContent)
                sleepInterval = SLEEP_INTERVAL
            } catch (e: VkException) {
                log.info("Sleeping {} sec...", sleepInterval / 1000)
                Thread.sleep(sleepInterval)
                sleepInterval += SLEEP_INTERVAL
                continue
            }

            chunkNumber++
            Thread.sleep(200)
        }
    }

    private fun fetchUrlsChunk(userId: Long, audioList: List<Audio>) {
        val audioMap = audioList.associateBy { it.id }.toSortedMap()
        val ids = audioList.joinToString { "${it.ownerId}_${it.id}" }

        val response = Jsoup.connect("$PATH_BASE/al_audio.php")
                .userAgent(USER_AGENT).cookies(cookies).method(Connection.Method.POST)
                .data("act", "reload_audio")
                .data("al", "1")
                .data("ids", ids)
                .execute()

        val body = response.body()
        if(!body.contains(JSON_DELIMITER)) {
            throw VkException("Can not fetch audio urls.")
        }

        val trimmed = body.substring(body.indexOf(JSON_DELIMITER) + JSON_DELIMITER.length)
        val json = trimmed.substring(0, trimmed.indexOf(BLOCK_DELIMITER))

        val list = jacksonObjectMapper().readValue<List<*>>(json, List::class.java)
        list.forEach {
            it as List<*>

            val audio = audioMap[(it[0] as Number).toLong()]
            audio!!.url = decrypt(userId, it[2] as String)
        }
    }

    private fun decrypt(vkId: Long, url: String): String {
        val script = this.script.replace("\${vkId}", vkId.toString()) // TODO: replace with bindings
        SCRIPT_ENGINE.eval(script)

        return (SCRIPT_ENGINE as Invocable).invokeFunction("decode", url) as String
    }


    private data class VkAudioDto(
            val type: String,
            val ownerId: Long,
            val albumId: Int,
            val title: String,
            val hasMore: Int,
            val nextOffset: Int,
            val totalCount: Int,
            val list: List<List<*>>,
            val audio: List<Audio> = list.map {
                Audio(
                        (it[0] as Number).toLong(),
                        (it[1] as Number).toLong(),
                        StringEscapeUtils.unescapeHtml4(it[4] as String),
                        StringEscapeUtils.unescapeHtml4(it[3] as String),
                        it[5] as Int
                )
            }.toList()
    ) {
        fun hasMore(): Boolean {
            return hasMore == 1
        }
    }

}
