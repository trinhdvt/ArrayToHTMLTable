package Controller;

import Model.Database;
import Model.HTMLObject;
import Model.HTMLObjectTableModel;
import View.InputView;
import View.OutputView;

import javax.swing.*;
import java.awt.*;
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
    private final InputView inputView;
    private final OutputView outputView;
    private final Database database = Database.getInstance();
    private final JFileChooser fileChooser = new JFileChooser();
    private final HashSet<Long> myThreadId = new HashSet<>();
    private final HTMLObjectTableModel model;
    private HTMLObject editObject = null;
    private HTMLObject myObject = null;
    private boolean editMode = false;

    public Controller() {
        inputView = new InputView("ArrayToHTMLTable");
        outputView = new OutputView("Output Window");
        model = new HTMLObjectTableModel();
        initView();
        initModel();
        initController();
    }

    private void initView() {
        inputView.getHistoryTable().setModel(model);
        inputView.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        inputView.setVisible(true);

        Point x = inputView.getLocation();
        outputView.setLocation(new Point((int) (x.getX() + inputView.getWidth()), (int) x.getY()));
        outputView.setSize(new Dimension(inputView.getSize()));
        outputView.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        outputView.setVisible(true);

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
        inputView.getAboutMenu().addActionListener(l -> JOptionPane.showMessageDialog(inputView,
                "Trinh-dvt", "About", JOptionPane.INFORMATION_MESSAGE));
        inputView.getExitMenu().addActionListener(l -> inputView.dispatchEvent(new WindowEvent(inputView, WindowEvent.WINDOW_CLOSING)));
        inputView.getGettingStartedMenu().addActionListener(l -> {
            File doc = new File("src/Doc.txt");
            String absPath = doc.getAbsolutePath();
            try {
                new ProcessBuilder("Notepad.exe", absPath).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        inputView.getExportResult().addActionListener(l -> {
            if (fileChooser.showSaveDialog(inputView) == JFileChooser.APPROVE_OPTION) {
                Thread t = new Thread(() -> exportDataToFile(fileChooser.getSelectedFile()));
                t.setName("Export Data");
                myThreadId.add(t.getId());
                t.start();
            }
        });
//        inputView.getPrefsMenu().addActionListener(l -> inputView.getPrefsDialog().setVisible(true));
    }

    private void initWindowAction() {
        inputView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                Thread t = new Thread(() -> {
                    try {
                        database.connect();
                        database.loadDB();
                        refreshHistoryTable();
                    } catch (ClassNotFoundException | SQLException exception) {
                        JOptionPane.showMessageDialog(inputView,
                                "Cannot connect DB " + exception.getMessage(),
                                "Connect DB error!", JOptionPane.ERROR_MESSAGE);
                        inputView.dispose();
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
                    res = JOptionPane.showConfirmDialog(inputView,
                            "Something is running in background. Force to exit ?",
                            null, JOptionPane.OK_CANCEL_OPTION);
                    if (res == JOptionPane.CANCEL_OPTION)
                        return;
                } else
                    res = JOptionPane.showConfirmDialog(inputView, "Do you want to exit!",
                            "Exit Confirm", JOptionPane.OK_CANCEL_OPTION);
                if (res == JOptionPane.OK_OPTION) {
                    try {
                        database.disconnect();
                    } catch (SQLException ignored) {
                    } finally {
                        outputView.dispose();
                        inputView.dispose();
                    }
                }
            }
        });
    }

    private void initButtonAction() {
        inputView.getRunBtn().addActionListener(l -> {
            String rawInput = inputView.getInput().getText().trim();
            boolean header = inputView.getHeader().isSelected();
            boolean index = inputView.getIndex().isSelected();
            if ("".equals(rawInput))
                return;
            String[][] input = preProcessInput(rawInput);
            String date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            myObject = new HTMLObject(input, new boolean[]{header, index}, date);
            if (!outputView.isVisible()) outputView.setVisible(true);
            outputView.getOutputTA().setText(myObject.getTable());
        });

        inputView.getSaveBtn().addActionListener(l -> {
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
                JOptionPane.showMessageDialog(inputView, "Cannot save data",
                        "Save data error!", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputView.getClearBtn().addActionListener(l -> {
            inputView.getInput().setText("");
            outputView.getOutputTA().setText("");
            inputView.getHeader().setSelected(false);
            inputView.getIndex().setSelected(false);
            myObject = null;
        });

    }

    private void initTableAction() {
        inputView.getDeleteRow().addActionListener(l -> {
            JTable table = inputView.getHistoryTable();
            int id = (int) model.getValueAt(table.getSelectedRow(), 0);
            try {
                database.deleteObjectByID(id);
                refreshHistoryTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(inputView, e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        inputView.getEditRow().addActionListener(l -> {
            JTable table = inputView.getHistoryTable();
            int id = (int) model.getValueAt(table.getSelectedRow(), 0);
            editObject = database.findByID(id);
            editMode = true;
            inputView.getInput().setText(generateRawInput(editObject.getArr()));
            inputView.getHeader().setSelected(editObject.getHeader());
            inputView.getIndex().setSelected(editObject.getIndex());
            outputView.getOutputTA().setText(editObject.getTable());
            inputView.getTabPane().setSelectedIndex(0);
        });
        inputView.getHistoryTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JTable table = inputView.getHistoryTable();
                    int row = table.rowAtPoint(e.getPoint());
                    table.getSelectionModel().setSelectionInterval(row, row);
                    inputView.getPopupMenu().show(table, e.getX(), e.getY());
                }
            }
        });
    }

    private void refreshHistoryTable() {
        model.fireTableDataChanged();
    }

    private void exportDataToFile(File file) {
        if (myObject == null) {
            JOptionPane.showMessageDialog(inputView, "Nothing to export",
                    "Export information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(myObject.getJsonString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(inputView, e.getMessage(),
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
