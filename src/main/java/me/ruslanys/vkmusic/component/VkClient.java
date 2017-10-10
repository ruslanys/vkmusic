package me.ruslanys.vkmusic.component;

import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.exception.VkException;
import me.ruslanys.vkmusic.property.VkProperties;

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
