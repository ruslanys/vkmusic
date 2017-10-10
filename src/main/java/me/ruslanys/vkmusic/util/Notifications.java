package me.ruslanys.vkmusic.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.awt.*;

@Slf4j
public class Notifications {

    private Notifications() {
        throw new UnsupportedOperationException();
    }

    public static void showNotification(String message) {
        if (SystemUtils.IS_OS_LINUX) {
            showLinuxNotification(message);
        } else {
            showDefaultNotification(message);
        }
    }

    private static void showDefaultNotification(String message) {
        SystemTray.getSystemTray().getTrayIcons()[0]
                .displayMessage("VKMusic", message, TrayIcon.MessageType.INFO);
    }

    private static void showLinuxNotification(String message) {

        try {
            Process process = new ProcessBuilder("notify-send", "-i", "user-info", "VKMusic", message)
                    .start();
            process.waitFor();
        } catch (Exception e) {
            showDefaultNotification(message);
        }
    }

}
