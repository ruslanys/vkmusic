package me.ruslanys.vkaudiosaver.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data

@Entity
public class Audio {

    @Id
    private Integer id;

    @Column
    private String artist;

    @Column
    private String title;

    @Column
    private Integer duration;

    @Transient
    private String url;

    public static String getFilename(String destination, Audio audio) {
        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotEmpty(destination)) {
            sb.append(destination);

            if (!destination.endsWith("/")) sb.append("/");
        }

        sb.append(StringUtils.substring(audio.getArtist().replace("/", "").replace("\\", ""), 0, 15));
        sb.append(" - ");
        sb.append(StringUtils.substring(audio.getTitle().replace("/", "").replace("\\", ""), 0, 20));

        sb.append(".mp3");

        return sb.toString()
                .replace("(", "")
                .replace(")", "");
    }

}
