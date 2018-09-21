package me.ruslanys.vkmusic

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
class EncodingTest {

    @Test
    fun stringDecodingTest() {
        val argument = "Ëÿïèñ Òðóáåöêîé"

        val decoded = String(argument.toByteArray(Charsets.ISO_8859_1), charset("windows-1251"))
        assertThat(decoded).isEqualTo("Ляпис Трубецкой")
    }

}
