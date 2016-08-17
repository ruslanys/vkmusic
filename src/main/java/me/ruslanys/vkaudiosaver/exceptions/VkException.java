package me.ruslanys.vkaudiosaver.exceptions;

import lombok.Data;
import me.ruslanys.vkaudiosaver.domain.vk.VkError;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
public class VkException extends Exception {

    private final VkError error;

    public VkException(VkError error) {
        this.error = error;
    }

}
