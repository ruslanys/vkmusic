package me.ruslanys.vkaudiosaver.ui;

import me.ruslanys.vkaudiosaver.component.VkClient;
import me.ruslanys.vkaudiosaver.property.VkProperties;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
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
// TODO: handle On Enter button
@Component
public class LoginFrame extends JFrame implements ActionListener {

    private final VkClient client;
    private final PropertyService propertyService;

    private JPasswordField passwordFld;
    private JTextField usernameFld;

    @Autowired
    public LoginFrame(VkClient client, PropertyService propertyService) throws IOException {
        this.client = client;
        this.propertyService = propertyService;
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

    private void setState(State state) {
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), state.name());
    }

    /**
     * Here is could be OnSubmit interface, for example.
     *
     * @deprecated Logic is gonna be moved to the Controller layer.
     */
    @Deprecated
    @Override
    public void actionPerformed(ActionEvent event) {
        final VkProperties properties = new VkProperties(usernameFld.getText(), passwordFld.getText());

        setState(State.LOADING);

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                client.auth(properties);
                propertyService.save(properties);
                client.getAudio();

                setVisible(false);
            } catch (Exception ex) {
                setState(State.LOGIN);

                JOptionPane.showMessageDialog(null,
                        ex.getClass().getSimpleName() + ":\r\n" + ex.getMessage(),
                        "Ошибка авторизации",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private enum State {
        LOGIN, LOADING
    }
}
