package me.ruslanys.vkmusic.entity.domain.event;

import org.springframework.context.ApplicationEvent;

import javax.swing.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public class LogoutEvent extends ApplicationEvent {
    public LogoutEvent(JFrame source) {
        super(source);
    }

    @Override
    public JFrame getSource() {
        return (JFrame) super.getSource();
    }
}
