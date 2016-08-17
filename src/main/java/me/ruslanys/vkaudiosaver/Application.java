package me.ruslanys.vkaudiosaver;

import me.ruslanys.vkaudiosaver.components.AudioDownloader;
import me.ruslanys.vkaudiosaver.components.PlaylistCreator;
import me.ruslanys.vkaudiosaver.components.VkApi;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.exceptions.VkException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        VkApi vkApi = context.getBean(VkApi.class);
        AudioDownloader audioDownloader = context.getBean(AudioDownloader.class);
        PlaylistCreator playlistCreator = context.getBean(PlaylistCreator.class);

        try {
            List<Audio> audios = vkApi.getAudio().getItems();
            audioDownloader.download(audios);
            playlistCreator.playlist("/home/ruslanys/Music", audios);
        } catch (VkException e) {
            System.out.println("SUKA LUBOFF");
        }


    }

}
