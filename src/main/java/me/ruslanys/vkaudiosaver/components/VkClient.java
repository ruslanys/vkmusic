package me.ruslanys.vkaudiosaver.components;

import me.ruslanys.vkaudiosaver.domain.vk.VkAudioResponse;
import me.ruslanys.vkaudiosaver.exceptions.VkException;
import me.ruslanys.vkaudiosaver.properties.VkProperties;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface VkClient {

    void init(VkProperties vkProperties) throws VkException;

    VkAudioResponse getAudio() throws VkException;

}
