package me.ruslanys.vkaudiosaver.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data

@ConfigurationProperties(prefix = "downloader", ignoreUnknownFields = false)
@Component
public class DownloaderProperties {

    private Integer poolSize = 5;
    private String destination;

}
