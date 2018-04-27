package me.ruslanys.vkmusic.service;

import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.entity.domain.event.DownloadStatusEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Deprecated
public interface AudioService {

    List<Audio> findAll();

    List<Audio> findFailed();

    List<Audio> fetchAll();

    void save(Audio audio);

    @EventListener
    void onDownloadStatusEvent(DownloadStatusEvent event);

}
