package me.ruslanys.vkaudiosaver.component;

import me.ruslanys.vkaudiosaver.domain.Audio;
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

    void fetchUrl(List<Audio> audioList);

}
