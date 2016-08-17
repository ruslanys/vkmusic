package me.ruslanys.vkaudiosaver.domain.vk;

import lombok.Data;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
public class VkError {

    private final Integer code;
    private final String message;

}
