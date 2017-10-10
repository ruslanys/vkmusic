package me.ruslanys.vkmusic.util;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class Dialogs {

    private Dialogs() {
        throw new UnsupportedOperationException();
    }

    public static void showError(Throwable ex) {
        if (ex instanceof RuntimeException) {
            log.error("Execution error", ex);
        }
        showError(ex.getClass().getSimpleName() + ":\r\n" + ex.getMessage());
    }

    public static void showError(String message) {
        showError("Ошибка", message);
    }

    public static void showError(String title, String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

}
