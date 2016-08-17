package me.ruslanys.vkaudiosaver.components;

import me.ruslanys.vkaudiosaver.domain.Audio;

import java.util.Collection;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface AudioDownloader {

    void download(String destination, Collection<Audio> audios);

    void download(Collection<Audio> audios);

}
