package me.ruslanys.vkmusic.property;

import lombok.Data;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
public class DownloaderProperties implements Properties {

    private String destination;
    private int poolSize = 5;

    private long autoSyncDelay = 60;
    private boolean autoSync = false;

}
