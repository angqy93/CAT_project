package org.example.xplorer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

public class ViewNavigator {

    public static void switchToPage(String fxml, Window window) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(ViewNavigator.class.getResource(fxml)));
            Scene scene = new Scene(root);
            Stage stage = (Stage) window;
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}