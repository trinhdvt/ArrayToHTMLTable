import Controller.Controller;
import Model.HTMLObjectTableModel;
import View.View;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Thread(() -> {
            View view = new View();
            HTMLObjectTableModel model = new HTMLObjectTableModel();
            new Controller(view, model);
        }));
    }
}
