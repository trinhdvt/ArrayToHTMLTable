package Controller;

import Model.HTMLObject;
import View.View;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Controller {
    private final View view;
    private HTMLObject myObject = null;

    public Controller(View view) {
        this.view = view;
        initController();
    }

    public void initController() {
        initMenuAction();
        view.getRunBtn().addActionListener(l -> {
            String input = view.getInput().trim();
            boolean header = view.getHeader();
            boolean index = view.getIndex();
            if ("".equals(input))
                return;
            myObject = new HTMLObject(preProcessInput(input), header, index);
            view.setOutput(myObject.toTable());
        });

    }

    private void initMenuAction() {
        view.getAboutMenu().addActionListener(l -> JOptionPane.showMessageDialog(view,
                "Trinh-dvt", "About", JOptionPane.INFORMATION_MESSAGE));
        view.getExitMenu().addActionListener(l -> view.dispose());
        view.getGettingStartedMenu().addActionListener(l -> {
            File doc = new File("src/Doc.txt");
            String absPath = doc.getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder("Notepad.exe", absPath);
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
}
