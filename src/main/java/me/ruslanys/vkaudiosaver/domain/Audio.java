package me.ruslanys.vkaudiosaver.domain;

import lombok.Data;

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

}
