package Controller;

import Model.Database;
import Model.HTMLObject;
import Model.HTMLObjectTableModel;
import View.View;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private final HTMLObjectTableModel model;
    private HTMLObject editObject = null;
    private HTMLObject myObject = null;
    private boolean editMode = false;

    public Controller(View view, HTMLObjectTableModel model) {
        this.view = view;
        this.model = model;
        initView();
        initModel();
        initController();
    }

    private void initView() {
        view.getHistoryTable().setModel(model);
    }

    private void initModel() {
        model.setMyObjects(database.getMyObjects());
    }

    private void initController() {
        initWindowAction();
        initMenuAction();
        initButtonAction();
        initTableAction();
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
                Thread t = new Thread(() -> exportDataToFile(fileChooser.getSelectedFile()));
                t.setName("Export Data");
                myThreadId.add(t.getId());
                t.start();
            }
        });
//        view.getPrefsMenu().addActionListener(l -> view.getPrefsDialog().setVisible(true));
    }

    private void initWindowAction() {
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                Thread t = new Thread(() -> {
                    try {
                        database.connect();
                        database.loadDB();
                        refreshHistoryTable();
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

            @Override
            public void windowClosing(WindowEvent e) {
                int res;
                final Set<Thread> threads = Thread.getAllStackTraces().keySet();
                if (threads.stream().anyMatch(t -> myThreadId.contains(t.getId()))) {
                    res = JOptionPane.showConfirmDialog(view,
                            "Something is running in background. Force to exit ?",
                            null, JOptionPane.OK_CANCEL_OPTION);
                    if (res == JOptionPane.CANCEL_OPTION)
                        return;
                } else
                    res = JOptionPane.showConfirmDialog(view, "Do you want to exit!",
                            "Exit Confirm", JOptionPane.OK_CANCEL_OPTION);
                if (res == JOptionPane.OK_OPTION) {
                    try {
                        database.disconnect();
                    } catch (SQLException ignored) {
                    }
                    view.dispose();
                }
            }
        });
    }

    private void initButtonAction() {
        view.getRunBtn().addActionListener(l -> {
            String rawInput = view.getInput().getText().trim();
            boolean header = view.getHeader().isSelected();
            boolean index = view.getIndex().isSelected();
            if ("".equals(rawInput))
                return;
            String[][] input = preProcessInput(rawInput);
            String date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            myObject = new HTMLObject(input, new boolean[]{header, index}, date);
            view.getOutput().setText(myObject.getTable());
        });

        view.getSaveBtn().addActionListener(l -> {
            if (myObject == null) return;
            if (editMode) {
                myObject.setId(editObject.getId());
                database.replaceByID(myObject);
                editMode = false;
            } else {
                myObject.setId(HTMLObject.ID_IDENTIFY++);
                database.addObject(myObject);
            }
            try {
                database.saveToDB(myObject);
                refreshHistoryTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(view, "Cannot save data",
                        "Save data error!", JOptionPane.ERROR_MESSAGE);
            }
        });

        view.getClearBtn().addActionListener(l -> {
            view.getInput().setText("");
            view.getOutput().setText("");
            view.getHeader().setSelected(false);
            view.getIndex().setSelected(false);
            myObject = null;
        });

    }

    private void initTableAction() {
        view.getDeleteRow().addActionListener(l -> {
            JTable table = view.getHistoryTable();
            int id = (int) model.getValueAt(table.getSelectedRow(), 0);
            try {
                database.deleteObjectByID(id);
                refreshHistoryTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(view, e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        view.getEditRow().addActionListener(l -> {
            JTable table = view.getHistoryTable();
            int id = (int) model.getValueAt(table.getSelectedRow(), 0);
            editObject = database.findByID(id);
            editMode = true;
            view.getInput().setText(generateRawInput(editObject.getArr()));
            view.getHeader().setSelected(editObject.getHeader());
            view.getIndex().setSelected(editObject.getIndex());
            view.getOutput().setText(editObject.getTable());
            view.getTabPane().setSelectedIndex(0);
        });
        view.getHistoryTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JTable table = view.getHistoryTable();
                    int row = table.rowAtPoint(e.getPoint());
                    table.getSelectionModel().setSelectionInterval(row, row);
                    view.getPopupMenu().show(table, e.getX(), e.getY());
                }
            }
        });
    }

    private void refreshHistoryTable() {
        model.fireTableDataChanged();
    }

    private void exportDataToFile(File file) {
        if (myObject == null) {
            JOptionPane.showMessageDialog(view, "Nothing to export",
                    "Export information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(myObject.getJsonString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, e.getMessage(),
                    "Errors occur", JOptionPane.ERROR_MESSAGE);
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

    private String generateRawInput(String[][] arr) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : arr) {
            String tmp = String.join(", ", row);
            sb.append(tmp).append("\n");
        }
        return sb.toString();
    }
}
