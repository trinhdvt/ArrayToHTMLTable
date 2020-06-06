package View;

import javax.swing.*;
import java.awt.*;

public class OutputView extends JFrame {
    private JTextArea outputTA;

    public OutputView(String title) {
        super(title);
        this.setSize(600, 600);

        initComponent();
    }

    private void initComponent() {
        this.setLayout(new BorderLayout());
        JPanel panel1 = new JPanel(new GridLayout(1, 1));
        panel1.setBorder(BorderFactory.createTitledBorder("Output"));
        outputTA = new JTextArea(1, 1);
        outputTA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        outputTA.setFont(new Font("Fira Code Medium", Font.PLAIN, 16));
        outputTA.setEditable(false);
        outputTA.setLineWrap(true);
        panel1.add(new JScrollPane(outputTA));
        this.getContentPane().add(panel1, BorderLayout.CENTER);
    }

    public JTextArea getOutputTA() {
        return outputTA;
    }
}
