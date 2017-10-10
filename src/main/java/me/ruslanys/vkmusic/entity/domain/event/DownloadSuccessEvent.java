package me.ruslanys.vkmusic.entity.domain.event;

import lombok.Getter;
import me.ruslanys.vkmusic.component.impl.DownloadTask;
import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.entity.domain.DownloadStatus;

public class DownloadSuccessEvent extends DownloadStatusEvent {

    @Getter
    private final DownloadTask.Result result;

    public DownloadSuccessEvent(Object source, DownloadTask.Result downloadResult) {
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
