package me.ruslanys.vkmusic.ui.view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public abstract class LoadingFrame extends JFrame {

    private final Color bgColor;
    private final URL spinner;


    public LoadingFrame() {
        this(new Color(80, 114, 153), LoadingFrame.class.getClassLoader().getResource("images/loading-heart.gif"));
    }

    public LoadingFrame(Color bgColor, URL spinner) {
        this.bgColor = bgColor;
        this.spinner = spinner;

        initComponents();
        initWindow();
    }

    private void initComponents() {
        JComponent loginPanel = initMainPanel();
        JComponent loadingPanel = initLoadingPanel();

        getContentPane().setLayout(new CardLayout());
        add(loginPanel, State.MAIN.name());
        add(loadingPanel, State.LOADING.name());
    }

    /**
     * In this method window size, title, position and close button behaviour are set up.
     *
     * Need to override usually.
     */
    protected void initWindow() {
        setTitle(getClass().getSimpleName());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Main panel method creation.
     * @return content panel
     */
    protected abstract JComponent initMainPanel();

    /**
     * Loading panel method creation.
     * @return loading panel
     */
    protected JComponent initLoadingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(bgColor);
        panel.add(new JLabel(new ImageIcon(spinner)), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Changing window state to show Main panel or loading spinner.
     * @param state window state
     */
    public final void setState(LoginFrame.State state) {
        if (SwingUtilities.isEventDispatchThread()) {
            CardLayout cl = (CardLayout) (getContentPane().getLayout());
            cl.show(getContentPane(), state.name());
        } else {
            SwingUtilities.invokeLater(() -> setState(state));
        }
    }

    public enum State {
        MAIN, LOADING
    }

}
