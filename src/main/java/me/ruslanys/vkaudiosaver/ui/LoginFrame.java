package me.ruslanys.vkaudiosaver.ui;

import me.ruslanys.vkaudiosaver.util.Dialogs;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NORTH;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class LoginFrame extends JFrame implements ActionListener {

    private JPasswordField passwordFld;
    private JTextField usernameFld;

    private OnSubmitListener submitListener;

    public LoginFrame() throws IOException {
        initComponents();
    }

    private void initComponents() throws IOException {
        setTitle("Авторизация");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel loginPanel = initLoginPanel();
        JPanel loadingPanel = initLoadingPanel();

        getContentPane().setLayout(new CardLayout());
        add(loginPanel, State.LOGIN.name());
        add(loadingPanel, State.LOADING.name());

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel initLoginPanel() {
        JPanel panel = new JPanel();
        Insets padding = new Insets(0, 5, 5, 5);

        panel.setLayout(new GridBagLayout());

        // username
        JLabel usernameLbl = new JLabel("Имя пользователя:");
        panel.add(usernameLbl, new GridBagConstraints(0, 0, 1, 1, 1., 0., NORTH, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        usernameFld = new JTextField();
        panel.add(usernameFld, new GridBagConstraints(0, 1, 1, 1, 1., 0., NORTH, HORIZONTAL, padding, 0, 0));

        // password
        JLabel passwordLbl = new JLabel("Пароль:");
        panel.add(passwordLbl, new GridBagConstraints(0, 2, 1, 1, 1., 0., NORTH, HORIZONTAL, padding, 0, 0));

        passwordFld = new JPasswordField();
        panel.add(passwordFld, new GridBagConstraints(0, 3, 1, 1, 1., 0., NORTH, HORIZONTAL, padding, 0, 0));

        // submit
        JButton loginBtn = new JButton("Войти");
        loginBtn.addActionListener(this);
        panel.add(loginBtn, new GridBagConstraints(0, 4, 1, 1, 1., 0., NORTH, HORIZONTAL, new Insets(5, 5, 10, 5), 0, 0));
        return panel;
    }

    private JPanel initLoadingPanel() {
        URL spinner = getClass().getClassLoader().getResource("images/loading.gif");

        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.add(new JLabel(new ImageIcon(spinner)), BorderLayout.CENTER);
        return panel;
    }

    public void setState(State state) {
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), state.name());
    }

    public void setSubmitListener(OnSubmitListener submitListener) {
        this.submitListener = submitListener;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final String username = usernameFld.getText();
        final String password = passwordFld.getText();

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            Dialogs.showError("Укажите имя пользователя и пароль!");
            return;
        }

        Executors.newSingleThreadExecutor().submit(() -> {
            setState(State.LOADING);

            if (submitListener != null) {
                try {
                    submitListener.onSubmit(username, password);
                } catch (Exception e) {
                    Dialogs.showError(e);
                }
            }

            setState(State.LOGIN);
        });
    }

    public enum State {
        LOGIN, LOADING
    }

    public interface OnSubmitListener {

        void onSubmit(String username, String password);

    }

}
