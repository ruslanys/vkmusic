package me.ruslanys.vkaudiosaver.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VkProperties extends Properties {

    private String username;
    private String password;

}
