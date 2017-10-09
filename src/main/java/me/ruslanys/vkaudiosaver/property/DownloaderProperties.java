package me.ruslanys.vkaudiosaver.property;

import lombok.Data;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
public class DownloaderProperties {

    private Integer poolSize = 5;
    private String destination;

    public DownloaderProperties(String destination) {
        this.destination = destination;
    }

}
