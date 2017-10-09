package me.ruslanys.vkaudiosaver.property;

import lombok.Data;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
public class DownloaderProperties {

    private String destination = "./Music/";
    private Integer poolSize = 5;

}
