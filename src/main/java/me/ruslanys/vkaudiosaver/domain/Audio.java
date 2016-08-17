package me.ruslanys.vkaudiosaver.domain;

import lombok.Data;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
public class Audio {

    private Long id;

    private String artist;

    private String title;

    private Integer duration;

    private String url;

    private Integer genre;

}
