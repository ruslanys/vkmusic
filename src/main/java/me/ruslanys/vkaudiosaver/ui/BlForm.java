package me.ruslanys.vkaudiosaver.ui;

import javax.swing.*;
import java.awt.*;

public class BlForm {
    private JPanel panel1;
    private JTable table1;
    private JButton указатьПапкуButton;
    private JButton сменитьПользователяButton;
    private JButton выходButton;
    private JButton обновитьButton;
    private JButton button1;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel1.add(panel2, BorderLayout.SOUTH);
        указатьПапкуButton = new JButton();
        указатьПапкуButton.setText("Указать папку");
        panel2.add(указатьПапкуButton);
        обновитьButton = new JButton();
        обновитьButton.setText("Обновить");
        panel2.add(обновитьButton);
        сменитьПользователяButton = new JButton();
        сменитьПользователяButton.setText("Сменить пользователя");
        panel2.add(сменитьПользователяButton);
        выходButton = new JButton();
        выходButton.setText("Выход");
        panel2.add(выходButton);
        table1 = new JTable();
        panel1.add(table1, BorderLayout.CENTER);
        final JToolBar toolBar1 = new JToolBar();
        panel1.add(toolBar1, BorderLayout.NORTH);
        button1 = new JButton();
        button1.setText("Button");
        toolBar1.add(button1);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
