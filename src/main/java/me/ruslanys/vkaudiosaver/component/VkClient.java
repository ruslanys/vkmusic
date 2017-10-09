package me.ruslanys.vkaudiosaver.component;

import me.ruslanys.vkaudiosaver.entity.Audio;
import me.ruslanys.vkaudiosaver.exception.VkException;
import me.ruslanys.vkaudiosaver.property.VkProperties;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface VkClient {

    void auth(VkProperties properties) throws VkException;

    void clear();

    List<Audio> getAudio();

    void fetchUrls(List<Audio> audioList);

}
