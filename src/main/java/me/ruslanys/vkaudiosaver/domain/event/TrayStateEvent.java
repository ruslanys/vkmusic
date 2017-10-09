package me.ruslanys.vkaudiosaver.domain.event;

import lombok.Getter;
import me.ruslanys.vkaudiosaver.component.impl.TrayHandler;
import org.springframework.context.ApplicationEvent;

import java.awt.event.ActionListener;

public class TrayStateEvent extends ApplicationEvent {

    @Getter
    private final TrayHandler.State state;

    @Getter
    private final ActionListener actionListener;

    public TrayStateEvent(Object source, TrayHandler.State state) {
        this(source, state, null);
    }

    public TrayStateEvent(Object source, TrayHandler.State state, ActionListener actionListener) {
        super(source);
        this.state = state;
        this.actionListener = actionListener;
    }

}
