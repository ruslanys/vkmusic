package me.ruslanys.vkaudiosaver.ui;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class MainFrame extends JFrame {

    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;

    public MainFrame() {
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        init0();

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

//        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenu2.setText("File");
        jMenuBar1.add(jMenu2);

        jMenu1.setText("jMenu1");

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");
        jMenu1.add(jCheckBoxMenuItem1);

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("jRadioButtonMenuItem1");
        jMenu1.add(jRadioButtonMenuItem1);

        jMenuItem2.setText("jMenuItem2");
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Edit");

        jMenuItem1.setText("jMenuItem1");
        jMenu3.add(jMenuItem1);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);
    }


    private JPanel panel1;
    private JTable table1;
    private JButton button1;

    private void init0() {
        setSize(400, 300);
        setLocationRelativeTo(null);

        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, BorderLayout.CENTER);
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        toolBar1.setMargin(new Insets(0, 5, 0, 0));
        panel1.add(toolBar1, BorderLayout.SOUTH);
        final JLabel label1 = new JLabel();
        label1.setText("Label");
        toolBar1.add(label1);

        setContentPane(panel1);
    }

}
