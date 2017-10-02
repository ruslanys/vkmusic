package me.ruslanys.vkaudiosaver.services;

import lombok.NonNull;
import me.ruslanys.vkaudiosaver.property.DownloaderProperties;
import me.ruslanys.vkaudiosaver.property.VkProperties;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface PropertyService {

    void save(@NonNull VkProperties vkProperties);

    void save(@NonNull DownloaderProperties downloaderProperties);

    VkProperties getVkProperties();

    DownloaderProperties getDownloaderProperties();

}
