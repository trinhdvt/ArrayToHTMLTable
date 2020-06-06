import Controller.MyController;

import javax.swing.*;
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Thread(MyController::new));
    }
}
