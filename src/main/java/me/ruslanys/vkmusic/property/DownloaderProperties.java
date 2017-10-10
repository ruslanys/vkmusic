package me.ruslanys.vkmusic.property;

import lombok.Data;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
public class DownloaderProperties extends Properties {

    private String destination;
    private int poolSize = 5;

}
