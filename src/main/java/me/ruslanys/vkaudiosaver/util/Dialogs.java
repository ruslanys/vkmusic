package me.ruslanys.vkaudiosaver.util;

import javax.swing.*;

public class Dialogs {

    public static void showError(Exception ex) {
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
