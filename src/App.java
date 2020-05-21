import Controller.Controller;
import View.View;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Thread(() -> new Controller(new View())));

    }
}
