package me.ruslanys.vkaudiosaver.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
public class Audio {

    private Integer id;

    private String artist;

    private String title;

    private Integer duration;

    private String url;

    public static String getFilename(String destination, Audio audio) {
        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotEmpty(destination)) {
            sb.append(destination);

            if (!destination.endsWith("/")) sb.append("/");
        }

        sb.append(StringUtils.substring(audio.getArtist(), 0, 15));
        sb.append(" - ");
        sb.append(StringUtils.substring(audio.getTitle(), 0, 20));

        sb.append(".mp3");

        return sb.toString();
    }

}
