package me.ruslanys.vkaudiosaver.exception;

import lombok.Data;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
public class VkException extends Exception {

    public VkException(String message) {
        super(message);
    }

}
