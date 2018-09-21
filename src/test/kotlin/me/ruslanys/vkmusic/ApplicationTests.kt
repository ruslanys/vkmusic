package me.ruslanys.vkmusic

import javafx.embed.swing.JFXPanel
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@Ignore
@RunWith(SpringRunner::class)
@SpringBootTest
class ApplicationTests {

    @Test
    fun contextLoads() {
    }


    companion object {

        @BeforeClass
        @JvmStatic
        fun before() {
            JFXPanel()
            // or
            // com.sun.javafx.application.PlatformImpl.startup { }
            // take a look at https://stackoverflow.com/questions/14025718/javafx-toolkit-not-initialized-when-trying-to-play-an-mp3-file-through-mediap
        }

    }

}
