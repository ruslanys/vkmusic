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
public class VkCookies implements Properties {

    private String sessionId;

}
