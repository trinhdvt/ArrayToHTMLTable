package View;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    private JTextArea inputTA, outputTA;
    private JButton runBtn, saveBtn, saveAsBtn;
    private JMenuItem exitMenu, aboutMenu, gettingStarted, exportResult;
    private JCheckBox headerCB, indexCB;

    public View() {
        this.setTitle("ArrayToHTMLTable");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
        headerCB = new JCheckBox("True");
        indexCB = new JCheckBox("True");
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(5, 2, 5, 5);
        gc.gridx = 0;
        gc.gridy = 0;
        panel1_1.add(new JLabel("Header"), gc);
        gc.gridx++;
        panel1_1.add(headerCB, gc);
        gc.gridx = 0;
        gc.gridy++;
        panel1_1.add(new JLabel("Index"), gc);
        gc.gridx++;
        gc.weightx = 8;
        panel1_1.add(indexCB, gc);
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
        saveBtn = new JButton("Save");
        saveAsBtn = new JButton("Save as");
        commandPanel.add(runBtn);
        commandPanel.add(saveBtn);
        commandPanel.add(saveAsBtn);

        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        this.getContentPane().add(commandPanel, BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        exitMenu = new JMenuItem("Exit");
        exportResult = new JMenuItem("Export...");
        fileMenu.add(exportResult);
        fileMenu.add(exitMenu);

        JMenu helpMenu = new JMenu("Help");
        aboutMenu = new JMenuItem("About");
        gettingStarted = new JMenuItem("Getting Started");
        helpMenu.add(gettingStarted);
        helpMenu.add(aboutMenu);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);
    }


    public String getInput() {
        return this.inputTA.getText();
    }

    public String getOutput() {
        return this.outputTA.getText();
    }

    public void setOutput(String output) {
        if (output != null)
            this.outputTA.setText(output);
    }

    public boolean getHeader() {
        return this.headerCB.isSelected();
    }

    public boolean getIndex() {
        return this.indexCB.isSelected();
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

    public JMenuItem getGettingStartedMenu() {
        return gettingStarted;
    }

    public JMenuItem getExportResult() {
        return exportResult;
    }

    public JButton getSaveBtn() {
        return saveBtn;
    }

    public JButton getSaveAsBtn() {
        return saveAsBtn;
    }
}
