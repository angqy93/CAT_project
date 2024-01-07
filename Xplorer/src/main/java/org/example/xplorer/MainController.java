package org.example.xplorer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void SwitchToHomePage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        // Get the primary screen
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();
        double screenHeight = screen.getVisualBounds().getHeight();

        // Set the scene size to match the screen size
        Scene scene = new Scene(root, screenWidth, screenHeight);

        stage.setScene(scene);
        stage.show();
    }
    public void SwitchToCreatePage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Create.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        // Get the primary screen
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();
        double screenHeight = screen.getVisualBounds().getHeight();

        // Set the scene size to match the screen size
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.show();
    }

    public void SwitchToSearchPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Search.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        // Get the primary screen
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();
        double screenHeight = screen.getVisualBounds().getHeight();

        // Set the scene size to match the screen size
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private Button displayEventButton;

    public void connectDisplayEventButton(ActionEvent event) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String connectQuery = "SELECT * FROM eventdata";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            StringBuilder resultText = new StringBuilder();

            while (queryOutput.next()) {
                // Assuming 'event_name' is a column in your database table
                String eventName = queryOutput.getString("event_name");

                // Append each event name to the result text
                resultText.append(eventName).append("\n");
            }

            // Set the concatenated result text to the button or another UI element
            displayEventButton.setText(resultText.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
