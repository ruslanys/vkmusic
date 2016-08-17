package me.ruslanys.vkaudiosaver.components.impl;

import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.components.PlaylistCreator;
import me.ruslanys.vkaudiosaver.domain.Audio;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class M3uPlaylistCreator implements PlaylistCreator {

//    @SneakyThrows
    @Override
    public void playlist(String destination, List<Audio> audios) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destination + "/" + "playlist.m3u")))) {
            writer.write("#EXTM3U");
            writer.newLine();
            writer.newLine();

            for (Audio audio : audios) {
                writer.write(String.format("#EXTINF:%d, %s - %s", audio.getDuration(), audio.getArtist(), audio.getTitle()));
                writer.newLine();

                writer.write(Audio.getFilename(null, audio));
                writer.newLine();
                writer.newLine();
            }

            writer.flush();

        } catch (IOException e) {
            log.error("Exception due playlist creation process.");
        }
    }

}
