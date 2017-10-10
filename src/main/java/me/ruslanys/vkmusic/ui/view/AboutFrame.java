package me.ruslanys.vkmusic.ui.view;

import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkmusic.util.DesktopUtils;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Component
public class AboutFrame extends JFrame implements HyperlinkListener {

    public AboutFrame() {
        initComponents();
        initWindow();
    }

    private void initWindow() {
        setTitle("О программе");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBorder(new EmptyBorder(10, 10, 0, 10));

        // text pane
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(getContent());
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setBackground(null);
        textPane.setBorder(null);
        textPane.addHyperlinkListener(this);
        textPane.setBorder(new EmptyBorder(0, 0, 5, 10));
        panel.add(textPane, BorderLayout.CENTER);

        // navigation panel
        final JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 15));
        panel.add(navPanel, BorderLayout.SOUTH);

        JButton btnOk = new JButton("OK");
        btnOk.setPreferredSize(new Dimension(100, 30));
        btnOk.addActionListener(e -> AboutFrame.this.dispose());
        navPanel.add(btnOk);

        // --
        setContentPane(panel);
    }

    private String getContent() {
        InputStream resource = getClass().getClassLoader().getResourceAsStream("about.html");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error("Can not load about content", e);
        }

        return sb.toString();
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (HyperlinkEvent.EventType.ACTIVATED.equals(event.getEventType())) {
            DesktopUtils.browse(event.getURL());
        }
    }
}
