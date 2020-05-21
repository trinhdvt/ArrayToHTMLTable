package Controller;

import Model.HTMLObject;
import View.View;

import javax.swing.*;

public class Controller {
    private final View view;
    private HTMLObject myObject;

    public Controller(View view) {
        this.view = view;
        initController();
    }

    public void initController() {
        view.getAboutMenu().addActionListener(l -> JOptionPane.showMessageDialog(view,
                "Trinh-dvt", "About", JOptionPane.INFORMATION_MESSAGE));
        view.getExitMenu().addActionListener(l -> view.dispose());
        view.getRunBtn().addActionListener(l -> {
            String input = view.getInput().trim();
            boolean header = view.getHeader();
            boolean index = view.getIndex();
            myObject = new HTMLObject(preProcessInput(input), header, index);
            view.setOutput(myObject.toTable());
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
