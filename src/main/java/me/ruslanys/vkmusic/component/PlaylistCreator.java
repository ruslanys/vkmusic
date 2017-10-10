package me.ruslanys.vkmusic.component;

import me.ruslanys.vkmusic.entity.Audio;

import java.util.Collection;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface PlaylistCreator {

    void playlist(String destination, Collection<Audio> audios);

}
