package me.ruslanys.vkmusic.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.ruslanys.vkmusic.entity.domain.DownloadStatus;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
@EqualsAndHashCode(of = {"id", "ownerId"})
@NoArgsConstructor

@Entity
public class Audio implements Serializable {

    @Id
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

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


    public Audio(@NonNull Long id, @NonNull Long ownerId, @NonNull String artist, @NonNull String title, @NonNull Integer duration) {
        this.id = id;
        this.ownerId = ownerId;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
    }

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
