package me.ruslanys.vkaudiosaver.domain.vk;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.ruslanys.vkaudiosaver.domain.Audio;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
@NoArgsConstructor
@ToString(exclude = "items")
public class VkAudioResponse extends VkResponse {

    private Integer count;
    private List<Audio> items;


    public VkAudioResponse(Integer count, List<Audio> items) {
        this.count = count;
        this.items = items;
    }

}
