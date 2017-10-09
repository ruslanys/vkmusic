package me.ruslanys.vkaudiosaver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ruslanys.vkaudiosaver.entity.domain.DownloadStatus;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
@EqualsAndHashCode(exclude = {"url", "status", "position"})

@Entity
public class Audio {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DownloadStatus status = DownloadStatus.NEW;

    @Transient
    private String url;

    public String getFilename() {
        StringBuilder sb = new StringBuilder();

        String formattedArtist = getArtist().trim().replaceAll("[!\"#$%&'()*+,\\-/:;<=>?@\\[\\]^_`{|}~]", "");
        String formattedTitle = getTitle().trim().replaceAll("[!\"#$%&'()*+,\\-/:;<=>?@\\[\\]^_`{|}~]", "");

        sb.append(StringUtils.substring(formattedArtist, 0, 15));
        sb.append(" - ");
        sb.append(StringUtils.substring(formattedTitle, 0, 20));

        sb.append(".mp3");

        return sb.toString();
    }

}
