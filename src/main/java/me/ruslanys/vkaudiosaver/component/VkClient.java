package me.ruslanys.vkaudiosaver.component;

import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.exception.VkException;
import me.ruslanys.vkaudiosaver.property.VkProperties;

import java.io.IOException;
import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface VkClient {

    void auth(VkProperties properties) throws VkException;

    List<Audio> getAudio();

    void getUrls(List<Audio> audioList) throws IOException, InterruptedException;
}
