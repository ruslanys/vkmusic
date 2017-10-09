package me.ruslanys.vkaudiosaver.component.impl;

import lombok.SneakyThrows;
import me.ruslanys.vkaudiosaver.entity.domain.event.TrayStateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;

@Component
public class TrayHandler {

    private volatile TrayIcon trayIcon;

    @EventListener
    @SneakyThrows
    private void displayTray(TrayStateEvent stateEvent) {
        Image image = loadImage(stateEvent.getState());

        if (trayIcon == null) {
            SystemTray tray = SystemTray.getSystemTray();

            trayIcon = new TrayIcon(image, "VKMusic");
            trayIcon.setImageAutoSize(true);

            tray.add(trayIcon);
        } else {
            trayIcon.setImage(image);
        }

        removeListeners(trayIcon);
        if (stateEvent.getActionListener() != null) {
            trayIcon.addActionListener(stateEvent.getActionListener());
        }
    }

    private void removeListeners(TrayIcon trayIcon) {
        for (ActionListener listener : trayIcon.getActionListeners()) {
            trayIcon.removeActionListener(listener);
        }
    }

    @SneakyThrows
    private Image loadImage(State state) {
        // Image image = Toolkit.getDefaultToolkit().createImage(getClass().getClassLoader().getResource("images/icon.png"));
        return ImageIO.read(getClass().getClassLoader().getResource("images/tray/" + state.name().toLowerCase() + ".png"));
    }

    public enum State {
        BASE, ADD, ERROR, INFO, LOADING, OK, PAUSE, STOP
    }

}
