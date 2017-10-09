package me.ruslanys.vkaudiosaver.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
@EqualsAndHashCode(exclude = {"url", "status"})

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
    private Status status = Status.NEW;

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

    public enum Status {
        NEW, DOWNLOADED, SKIPPED
    }

}
