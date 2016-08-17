package me.ruslanys.vkaudiosaver;

import com.google.common.collect.Lists;
import me.ruslanys.vkaudiosaver.components.AudioDownloader;
import me.ruslanys.vkaudiosaver.components.VkApi;
import me.ruslanys.vkaudiosaver.exceptions.VkException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        VkApi vkApi = context.getBean(VkApi.class);
        AudioDownloader audioDownloader = context.getBean(AudioDownloader.class);

        try {
            audioDownloader.download(Lists.newArrayList(vkApi.getAudio().getItems()));
        } catch (VkException e) {
            System.out.println("SUKA LUBOFF");
        }
    }

}
