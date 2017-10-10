package me.ruslanys.vkmusic.entity.domain.event;

import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.entity.domain.DownloadStatus;

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
