package me.ruslanys.vkaudiosaver.services;

import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.property.DownloaderProperties;

import java.util.Collection;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface DownloadService {

    @Deprecated
    void init(DownloaderProperties properties);

    void download(Collection<Audio> audios);

}
