package me.ruslanys.vkaudiosaver.util;

import lombok.SneakyThrows;

import java.awt.*;

public class Notifications {

    @SneakyThrows
    public static void ubuntuHello() {
        Runtime.getRuntime().exec("notify-send Hello world!");
    }

    public static void windowsHello() {
        SystemTray.getSystemTray().getTrayIcons()[0]
                .displayMessage("Hello", "World", TrayIcon.MessageType.INFO);
        // trayIcon.displayMessage("Yep!", "I'm here", TrayIcon.MessageType.NONE);

    }

//    public static void showFormValidationAlert(String message) {
//        JOptionPane.showMessageDialog(null,
//                message,
//                ConstMessagesEN.Messages.INFORMATION_TITLE,
//                JOptionPane.INFORMATION_MESSAGE);
//    }
//
//    public static void showTableRowNotSelectedAlert() {
//        JOptionPane.showMessageDialog(null,
//                ConstMessagesEN.Messages.NON_ROW_SELECTED,
//                ConstMessagesEN.Messages.ALERT_TILE,
//                JOptionPane.ERROR_MESSAGE);
//    }
//
//    public static void showDeleteRowErrorMessage() {
//        JOptionPane.showMessageDialog(null,
//                ConstMessagesEN.Messages.DELETE_ROW_ERROR,
//                ConstMessagesEN.Messages.ALERT_TILE,
//                JOptionPane.ERROR_MESSAGE);
//    }
}
