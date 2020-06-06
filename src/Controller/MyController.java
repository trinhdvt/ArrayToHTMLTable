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
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MyController {
    private final InputView inputView;
    private final OutputView outputView;
    private final Database database = Database.getInstance();
    private final JFileChooser fileChooser = new JFileChooser();
    private final HashSet<Long> myThreadId = new HashSet<>();
    private final HTMLObjectTableModel model;
    //    private final GettingStartedView gettingStartedView;
    private HTMLObject editObject = null;
    private HTMLObject myObject = null;
    private boolean editMode = false;

    public MyController() {
        inputView = new InputView("ArrayToHTMLTable");
        outputView = new OutputView("Output Window");
//        gettingStartedView = new GettingStartedView("Getting Started");
        model = new HTMLObjectTableModel();
        initView();
        initModel();
        initController();
    }

/*
    public static String readDocument() {
        File file = new File("Doc.txt");
        StringBuilder doc = new StringBuilder();
        String line;

        try (FileReader fileReader = new FileReader(file);
             BufferedReader br = new BufferedReader(fileReader)) {
            while ((line = br.readLine()) != null) {
                doc.append(line).append("\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return doc.toString();
    }
*/

    private void initView() {
//        gettingStartedView.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        inputView.getHistoryTable().setModel(model);
        inputView.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Point x = inputView.getLocation();
        outputView.setLocation(new Point((int) (x.getX() + inputView.getWidth()), (int) x.getY()));
        outputView.setSize(new Dimension(inputView.getSize()));
        outputView.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        inputView.setVisible(true);
        outputView.setVisible(true);
//        gettingStartedView.setLocationRelativeTo(null);
//        gettingStartedView.setVisible(true);
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

        });
        inputView.getExportResult().addActionListener(l -> {
            if (myObject == null) {
                JOptionPane.showMessageDialog(inputView, "Nothing to export",
                        "Export information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (fileChooser.showSaveDialog(inputView) == JFileChooser.APPROVE_OPTION) {
                Thread t = new Thread(() -> {
                    try {
                        exportDataToFile(fileChooser.getSelectedFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                t.setName("Export Data");
                myThreadId.add(t.getId());
                t.start();
            }
        });
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

    //Utils Function
    private void refreshHistoryTable() {
        model.fireTableDataChanged();
    }

    private void exportDataToFile(File file) throws IOException {
        String htmlTmp = readHTMLTemplate();
        String output = myObject.getTableAsHTML();
        String header = String.valueOf(myObject.getHeader());
        String index = String.valueOf(myObject.getIndex());
        String arr = Arrays.deepToString(myObject.getArr());
        htmlTmp = htmlTmp.replaceAll("@output", output)
                .replaceAll("@header", header)
                .replaceAll("@index", index)
                .replaceAll("@arr", arr);

        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(htmlTmp);
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

    private String readHTMLTemplate() throws IOException {
        File file = new File("template/IndexTemp.html");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
