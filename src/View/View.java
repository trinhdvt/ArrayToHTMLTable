package View;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    private JTextArea inputTA, outputTA;
    private JButton runBtn;
    private JMenuItem exitMenu, aboutMenu;

    public View() {
        this.setTitle("ArrayToHTMLTable");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);

        initComponents();
        createMenuBar();

        this.setVisible(true);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));

        JPanel panel1 = new JPanel(new BorderLayout(5, 5));
        panel1.setBorder(BorderFactory.createTitledBorder("Input"));
        panel1.add(new JLabel("2-D Array"), BorderLayout.WEST);
        inputTA = new JTextArea(1, 1);
        inputTA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel1.add(inputTA, BorderLayout.CENTER);

        JPanel panel1_1 = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(5, 2, 5, 5);
        gc.gridx = 0;
        gc.gridy = 0;
        panel1_1.add(new JLabel("Header"), gc);
        gc.gridx++;
        panel1_1.add(new JCheckBox("True"), gc);
        gc.gridx = 0;
        gc.gridy++;
        panel1_1.add(new JLabel("Index"), gc);
        gc.gridx++;
        gc.weightx = 8;
        panel1_1.add(new JCheckBox("True"), gc);
        panel1.add(panel1_1, BorderLayout.SOUTH);
        mainPanel.add(panel1);

        JPanel panel2 = new JPanel(new GridLayout(1, 1));
        panel2.setBorder(BorderFactory.createTitledBorder("Output"));
        outputTA = new JTextArea(1, 1);
        outputTA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        outputTA.setLineWrap(true);
        outputTA.setEditable(false);
        panel2.add(new JScrollPane(outputTA));
        mainPanel.add(panel2);

        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commandPanel.setBorder(BorderFactory.createTitledBorder("Command"));
        runBtn = new JButton("Run");
        commandPanel.add(runBtn);

        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        this.getContentPane().add(commandPanel, BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        exitMenu = new JMenuItem("Exit");
        fileMenu.add(exitMenu);

        JMenu helpMenu = new JMenu("Help");
        aboutMenu = new JMenuItem("About");
        helpMenu.add(aboutMenu);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);
    }

    public String getInput() {
        return this.inputTA.getText();
    }

    public void setOutput(String output) {
        if (output != null)
            this.outputTA.setText(output);
    }

    public JButton getRunBtn() {
        return runBtn;
    }

    public JMenuItem getExitMenu() {
        return exitMenu;
    }

    public JMenuItem getAboutMenu() {
        return aboutMenu;
    }

}
