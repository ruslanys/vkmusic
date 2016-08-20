package me.ruslanys.vkaudiosaver.services;

import me.ruslanys.vkaudiosaver.domain.Audio;

import java.util.Collection;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface DownloadService {

    void download(Collection<Audio> audios);

}
