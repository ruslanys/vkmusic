package me.ruslanys.vkaudiosaver.domain.event;

import lombok.Getter;
import me.ruslanys.vkaudiosaver.domain.Audio;
import org.springframework.context.ApplicationEvent;

public class AudioUpdatedEvent extends ApplicationEvent {

    @Getter
    private final Audio audio;

    public AudioUpdatedEvent(Object source, Audio audio) {
        super(source);
        this.audio = audio;
    }

}
