package Controller;

import Model.Database;
import Model.HTMLObject;
import View.View;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class Controller {
    private final View view;
    private final Database database = Database.getInstance();
    private final JFileChooser fileChooser = new JFileChooser();
    private final HashSet<Long> myThreadId = new HashSet<>();
    private HTMLObject myObject = null;

    public Controller(View view) {
        this.view = view;
        initController();
    }

    public void initController() {
        initWindowListener();
        initMenuAction();
        initButtonListener();
    }

    private void initMenuAction() {
        view.getAboutMenu().addActionListener(l -> JOptionPane.showMessageDialog(view,
                "Trinh-dvt", "About", JOptionPane.INFORMATION_MESSAGE));
        view.getExitMenu().addActionListener(l -> view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING)));
        view.getGettingStartedMenu().addActionListener(l -> {
            File doc = new File("src/Doc.txt");
            String absPath = doc.getAbsolutePath();
            try {
                new ProcessBuilder("Notepad.exe", absPath).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        view.getExportResult().addActionListener(l -> {
            if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
                Thread t = new Thread(() -> {
                    try {
                        exportDataToFile(fileChooser.getSelectedFile());
                    } catch (IOException e) {
                        JOptionPane.showInputDialog(view, "Cannot export data",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                t.setName("Export Data");
                myThreadId.add(t.getId());
                t.start();
            }
        });
    }

    private void initWindowListener() {
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    database.disconnect();
                } catch (SQLException ignored) {
                }
                int res;
                final Set<Thread> threads = Thread.getAllStackTraces().keySet();
                if (threads.stream().filter(t -> myThreadId.contains(t.getId())).toArray().length > 0) {
                    res = JOptionPane.showConfirmDialog(view,
                            "Something is running in background. Force to exit ?",
                            null, JOptionPane.OK_CANCEL_OPTION);
                    if (res == JOptionPane.CANCEL_OPTION)
                        return;
                } else
                    res = JOptionPane.showConfirmDialog(view, "Do you want to exit!",
                            "Exit Confirm", JOptionPane.OK_CANCEL_OPTION);
                if (res == JOptionPane.OK_OPTION) {
                    view.dispose();
                }
            }

            @Override
            public void windowOpened(WindowEvent e) {
                Thread t = new Thread(() -> {
                    try {
                        database.connect();
                    } catch (ClassNotFoundException | SQLException exception) {
                        JOptionPane.showMessageDialog(view,
                                "Cannot connect DB " + exception.getMessage(),
                                "Connect DB error!", JOptionPane.ERROR_MESSAGE);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ignore) {
                        }
                        view.dispose();
                    }
                });
                t.start();
                myThreadId.add(t.getId());
            }
        });
    }

    private void initButtonListener() {
        view.getRunBtn().addActionListener(l -> {
            String rawInput = view.getInput().trim();
            boolean header = view.getHeader();
            boolean index = view.getIndex();
            if ("".equals(rawInput))
                return;
            String[][] input = preProcessInput(rawInput);
            String date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            myObject = new HTMLObject(input, new boolean[]{header, index}, date);
            view.setOutput(myObject.getTable());
        });
        view.getSaveBtn().addActionListener(l -> {
            try {
                if (myObject != null)
                    database.saveToDB(myObject);
            } catch (SQLException ignored) {
                JOptionPane.showMessageDialog(view, "Cannot save data",
                        "Save data error!", JOptionPane.ERROR_MESSAGE);
            }
        });
        view.getSaveAsBtn().addActionListener(l -> {

        });
    }

    private void exportDataToFile(File file) throws IOException {
        if (myObject == null) {
            JOptionPane.showMessageDialog(view, "Nothing to export",
                    "Export information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(myObject.getJsonString());
        }
    }

    private String[][] preProcessInput(String rawInput) {
        String[] rows = rawInput.split("\n");
        String[][] arr = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            String[] cell = row.split(",");
            arr[i] = new String[cell.length];
            for (int j = 0; j < cell.length; j++) {
                String tmp = (cell[j].trim().length() > 0) ? cell[j].trim() : null;
                arr[i][j] = tmp;
            }
        }
        return arr;
    }

}
