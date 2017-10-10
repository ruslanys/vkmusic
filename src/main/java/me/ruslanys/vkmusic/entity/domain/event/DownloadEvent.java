package me.ruslanys.vkmusic.entity.domain.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public abstract class DownloadEvent extends ApplicationEvent {
    DownloadEvent(Object source) {
        super(source);
    }
}
