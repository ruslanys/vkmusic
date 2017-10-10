package me.ruslanys.vkmusic.services;

import me.ruslanys.vkmusic.entity.Audio;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface DownloadService {

    void download(List<Audio> audios);

    void downloadAsync(List<Audio> audios);

}
