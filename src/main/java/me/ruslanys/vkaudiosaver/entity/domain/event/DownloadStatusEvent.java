package me.ruslanys.vkaudiosaver.entity.domain.event;

import me.ruslanys.vkaudiosaver.entity.Audio;
import me.ruslanys.vkaudiosaver.entity.domain.DownloadStatus;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public abstract class DownloadStatusEvent extends DownloadEvent {
    DownloadStatusEvent(Object source) {
        super(source);
    }

    public abstract Audio getAudio();

    public abstract DownloadStatus getStatus();
}
