package me.ruslanys.vkaudiosaver;

import me.ruslanys.vkaudiosaver.components.VkClient;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.exceptions.VkException;
import me.ruslanys.vkaudiosaver.services.DownloadService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        // --
        VkClient vkClient = context.getBean(VkClient.class);
        DownloadService downloadService = context.getBean(DownloadService.class);

        try {
            List<Audio> audios = vkClient.getAudio().getItems();
            downloadService.download(audios);
        } catch (VkException e) {
            System.out.println("SUKA LUBOFF");
        }


    }

}
