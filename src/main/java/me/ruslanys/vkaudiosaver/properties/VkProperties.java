package me.ruslanys.vkaudiosaver.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data

@ConfigurationProperties(prefix = "vk", ignoreUnknownFields = false)
@Component
public class VkProperties {

    private String username;
    private String password;
    private String accessKey;

}
