package me.ruslanys.vkaudiosaver.components;

import me.ruslanys.vkaudiosaver.domain.Audio;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface PlaylistCreator {

    void playlist(String destination, List<Audio> audios);

}
