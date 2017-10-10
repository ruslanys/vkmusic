package me.ruslanys.vkmusic.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VkProperties implements Properties {

    private String username;
    private String password;

}
