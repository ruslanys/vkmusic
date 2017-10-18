package me.ruslanys.vkmusic.component;

import me.ruslanys.vkmusic.entity.Audio;

import java.util.List;
import java.util.Map;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface VkClient {

    void setCookies(Map<String, String> cookies);

    void addCookies(Map<String, String> cookies);

    void clearCookies();

    default List<Audio> getAudio() {
        Long userId = fetchUserId();
        return getAudio(userId);
    }

    List<Audio> getAudio(Long ownerId);

    Long fetchUserId();

    void fetchUrls(List<Audio> audioList);

}
