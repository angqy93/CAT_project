package org.example.xplorer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

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

    //List Events
    @FXML
    private ListView<String> eventListView;

    public void connectDisplayEventButton() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String connectQuery = "SELECT * FROM eventdata";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            ObservableList<String> eventDataList = FXCollections.observableArrayList();

            while (queryOutput.next()) {
                // Assuming 'event_name' is a column in your database table
                String eventName = queryOutput.getString("event_name");

                // Append each event name to the result text
                eventDataList.add(eventName);
            }

            // Set the concatenated result text to the ListView
            eventListView.setItems(eventDataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Call connectDisplayEventButton to initialize the data when the page is loaded
        connectDisplayEventButton();
    }

    //Click into Events
    @FXML
    private void handleEventListViewClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Check for double-click
            String selectedEvent = eventListView.getSelectionModel().getSelectedItem();

            if (selectedEvent != null) {
                // Call a method to show details of the selected event (e.g., description).
                showEventDetails(selectedEvent);
            }
        }
    }

    private void showEventDetails(String eventName) {
        // Implement the logic to display event details.
        // You can open a new window, dialog, or update another part of the UI.
        System.out.println("Selected Event: " + eventName);
        // Open a new window or dialog to display more details...
    }

    //ADD events
    @FXML
    private Button addEventButton;

    @FXML
    private void handleAddEventButton(ActionEvent event) {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Event");
        dialog.setHeaderText("Enter the event name:");
        dialog.setContentText("Event Name:");

        // Show the dialog and wait for the user's input
        Optional<String> result = dialog.showAndWait();

        // If the user entered a name, add it to the database and update the list
        result.ifPresent(eventName -> {
            // Add the event to the database
            addEventToDatabase(eventName);

            // Refresh the event list
            connectDisplayEventButton();
        });
    }

    private void addEventToDatabase(String eventName) {
        // Connect to the database and execute an INSERT query to add the new event
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Use a prepared statement to avoid SQL injection
        String insertQuery = "INSERT INTO eventdata (event_name, event_desc) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery);
            preparedStatement.setString(1, eventName);
            preparedStatement.setString(2, "wefwef");
            preparedStatement.executeUpdate();

            // Commit changes to the database
            connectDB.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception according to your application's needs
        } finally {
            // Close the database connection in a finally block to ensure it gets closed even if an exception occurs
            try {
                if (connectDB != null) {
                    connectDB.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception according to your application's needs
            }
        }
    }

    @FXML
    private void handleDeleteEventButton(ActionEvent event) {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Event");
        dialog.setHeaderText("Enter the event name to delete:");
        dialog.setContentText("Event Name:");

        // Show the dialog and wait for the user's input
        Optional<String> result = dialog.showAndWait();

        // If the user entered a name, delete the corresponding event from the database and update the list
        result.ifPresent(eventName -> {
            // Delete the event from the database
            deleteEventFromDatabase(eventName);

            // Refresh the event list
            connectDisplayEventButton();
        });
    }

    private void deleteEventFromDatabase(String eventName) {
        // Connect to the database and execute a DELETE query to remove the event
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Use a prepared statement to avoid SQL injection
        String deleteQuery = "DELETE FROM eventdata WHERE event_name = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(deleteQuery);
            preparedStatement.setString(1, eventName);
            preparedStatement.executeUpdate();

            // Commit changes to the database
            connectDB.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception according to your application's needs
        } finally {
            // Close the database connection in a finally block to ensure it gets closed even if an exception occurs
            try {
                if (connectDB != null) {
                    connectDB.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception according to your application's needs
            }
        }
    }
}

