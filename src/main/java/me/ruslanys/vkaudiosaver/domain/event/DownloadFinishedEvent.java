package me.ruslanys.vkaudiosaver.domain.event;

import lombok.Getter;
import me.ruslanys.vkaudiosaver.domain.Audio;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.List;

public class DownloadFinishedEvent extends ApplicationEvent {


    @Getter
    private final List<Audio> audioList;

    public DownloadFinishedEvent(Object source, List<Audio> audioList) {
        super(source);
        this.audioList = Collections.unmodifiableList(audioList);
    }

}
