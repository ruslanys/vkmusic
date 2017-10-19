package me.ruslanys.vkmusic.entity.domain.event;

import lombok.Getter;
import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.entity.domain.DownloadResult;
import me.ruslanys.vkmusic.entity.domain.DownloadStatus;

public class DownloadSuccessEvent extends DownloadStatusEvent {

    @Getter
    private final DownloadResult result;

    public DownloadSuccessEvent(Object source, DownloadResult downloadResult) {
        super(source);
        this.result = downloadResult;
    }

    @Override
    public Audio getAudio() {
        return result.getAudio();
    }

    @Override
    public DownloadStatus getStatus() {
        return DownloadStatus.SUCCESS;
    }
}
