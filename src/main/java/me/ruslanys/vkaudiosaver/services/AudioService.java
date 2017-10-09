package me.ruslanys.vkaudiosaver.services;

import me.ruslanys.vkaudiosaver.entity.Audio;
import me.ruslanys.vkaudiosaver.entity.domain.event.DownloadStatusEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface AudioService {

    List<Audio> findAll();

    List<Audio> fetchAll();

    void save(Audio audio);

    @EventListener
    void onDownloadStatusEvent(DownloadStatusEvent event);

}
