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
import java.util.Arrays;
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
        });
    }

    private void initButtonListener() {
        view.getRunBtn().addActionListener(l -> {
            String input = view.getInput().trim();
            boolean header = view.getHeader();
            boolean index = view.getIndex();
            if ("".equals(input))
                return;
            myObject = new HTMLObject(preProcessInput(input),
                    Calendar.getInstance().getTime().toString(),
                    header, index);
            view.setOutput(myObject.getTable());
        });
        view.getSaveBtn().addActionListener(l -> {
            createLocalDB();
            try {
                database.connect();
                database.disconnect();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

        });
    }

    private void exportDataToFile(File file) throws IOException {
        String input = view.getInput().trim();
        String output = view.getOutput().trim();
        if (input.isEmpty() || output.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nothing to export",
                    "Export information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("* Time: ");
            bw.write(Calendar.getInstance().getTime().toString() + "\n");
            bw.write("* Input:\n");
            bw.write("Array: " + Arrays.deepToString(myObject.getArr()) + "\n");
            bw.write("Header: " + myObject.getHeader() + "\n");
            bw.write("Index: " + myObject.getIndex() + "\n");
            bw.write("* Output:\n");
            bw.write(myObject.getTable());
        }
    }

    private String[][] preProcessInput(String input) {
        String[] rows = input.split("\n");
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

    private void createLocalDB() {
        String appPath = System.getProperty("user.dir");
        String dbName = "Local";
        try {
            Runtime.getRuntime().exec(String.format("cmd /c cd %s & md %s & attrib +h %s", appPath, dbName, dbName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
