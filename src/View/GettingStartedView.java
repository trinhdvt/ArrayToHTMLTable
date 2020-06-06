package View;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import javax.swing.*;

public class GettingStartedView extends JFrame {
    final JFXPanel fxPanel = new JFXPanel();
    private String document = null;
    private Button btn = null;

    public GettingStartedView(String title) {
        super(title);
        this.setSize(800, 500);
        this.add(fxPanel);

        Platform.runLater(this::initFXComponent);
    }

    private void initFXComponent() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(5, 5, 5, 5));
        Scene scene = new Scene(borderPane);
        this.fxPanel.setScene(scene);

        Label welcomeLabel = new Label("Array To HTML Table");
        welcomeLabel.setStyle("-fx-text-fill: #4b4a4a; " +
                "-fx-font-family: Fira Code Medium; " +
                "-fx-font-size: 26; " +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(three-pass-box, #00bfff, 10, 0.75, 3, 3)");
        BorderPane.setAlignment(welcomeLabel, Pos.TOP_CENTER);
        borderPane.setTop(welcomeLabel);


        TextArea textArea = new TextArea();
        textArea.setText(document);
        textArea.setEditable(false);
        textArea.setStyle("-fx-font-size: 16;-fx-font-family: Fira Code;-fx-font-weight: lighter");
        textArea.setMouseTransparent(true);
        textArea.setFocusTraversable(false);
        borderPane.setCenter(textArea);

        btn = new Button("Let's start");
        btn.setOnMouseClicked(event -> this.dispose());
        btn.setStyle("-fx-background-color: #2466ff;-fx-text-fill: #fff9fc;-fx-font-size: 16");
        BorderPane.setAlignment(btn, Pos.BOTTOM_RIGHT);
        borderPane.setBottom(btn);
    }

    public Button getBtn() {
        return btn;
    }

}
