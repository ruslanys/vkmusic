package me.ruslanys.vkaudiosaver.ui;

import me.ruslanys.vkaudiosaver.component.VkClient;
import me.ruslanys.vkaudiosaver.property.VkProperties;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Lazy
@Component
public class CredentialsFrame extends JFrame implements ActionListener {

    private final VkClient client;
    private final PropertyService propertyService;

    private JButton okBtn;
    private JPasswordField passwordFld;
    private JTextField usernameFld;

    @Autowired
    public CredentialsFrame(VkClient client, PropertyService propertyService) throws IOException {
        this.client = client;
        this.propertyService = propertyService;
        initComponents();
    }

    private void initComponents() throws IOException {
        setTitle("VKMusic");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        setMainLayout();
        pack();
    }

    private void setMainLayout() {
        okBtn = new JButton("Войти");
        okBtn.addActionListener(this);
        passwordFld = new JPasswordField();
        usernameFld = new JTextField();
        JLabel usernameLbl = new JLabel("Имя пользователя:");
        JLabel passwordLbl = new JLabel("Пароль:");

        getContentPane().removeAll();

        //region Generated code
        GroupLayout mainLayout = new GroupLayout(getContentPane());
        mainLayout.setHorizontalGroup(
                mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(okBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(passwordFld)
                                        .addComponent(usernameFld)
                                        .addGroup(mainLayout.createSequentialGroup()
                                                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(passwordLbl)
                                                        .addComponent(usernameLbl))
                                                .addGap(0, 150, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        mainLayout.setVerticalGroup(
                mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(usernameLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(usernameFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(passwordLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(passwordFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(okBtn)
                                .addContainerGap())
        );
        //endregion

        getContentPane().setLayout(mainLayout);

        revalidate();
        repaint();
    }

    private void setLoadingLayout() {
        getContentPane().removeAll();

        ImageIcon loadingIcon = new ImageIcon(getClass().getClassLoader().getResource("images/loading.gif"));
        JLabel loadingLbl = new JLabel(loadingIcon);

        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(loadingLbl, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final VkProperties properties = new VkProperties(usernameFld.getText(), passwordFld.getText());

        setLoadingLayout();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                client.auth(properties);
                propertyService.save(properties);

                setVisible(false);
            } catch (Exception ex) {
                setMainLayout();

                JOptionPane.showMessageDialog(null,
                        ex.getClass().getSimpleName() + ":\r\n" + ex.getMessage(),
                        "Ошибка авторизации",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
