package me.ruslanys.vkaudiosaver.domain.vk;

import lombok.Data;
import lombok.ToString;
import me.ruslanys.vkaudiosaver.domain.Audio;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
@ToString(exclude = "items")
public class VkAudioResponse extends VkResponse {

    private Integer count;

    private List<Audio> items;

}
