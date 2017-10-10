package me.ruslanys.vkmusic.entity.domain.event;

import lombok.Getter;
import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.entity.domain.DownloadStatus;
import me.ruslanys.vkmusic.exception.DownloadException;

public class DownloadFailEvent extends DownloadStatusEvent {

    @Getter
    private final DownloadException cause;

    public DownloadFailEvent(Object source, DownloadException cause) {
        super(source);
        this.cause = cause;
    }

    @Override
    public Audio getAudio() {
        return cause.getAudio();
    }

    @Override
    public DownloadStatus getStatus() {
        return DownloadStatus.FAIL;
    }
}
