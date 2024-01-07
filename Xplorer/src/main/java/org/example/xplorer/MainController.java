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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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


    //Delete Events
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

    //Update Events
    @FXML
    private void handleUpdateEventButton(ActionEvent event) {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Event");
        dialog.setHeaderText("Enter the current event name to update:");
        dialog.setContentText("Event Name:");

        // Show the dialog and wait for the user's input
        Optional<String> result = dialog.showAndWait();

        // If the user entered a name, proceed with the update
        result.ifPresent(currentEventName -> {
            // Get the selected event from the ListView
            Event selectedEvent = getEventByName(currentEventName);

            if (selectedEvent != null) {
                // Create a new TextInputDialog with the current event name as the default value
                TextInputDialog updateDialog = new TextInputDialog(selectedEvent.getEventName());
                updateDialog.setTitle("Update Event");
                updateDialog.setHeaderText("Enter the new event name:");
                updateDialog.setContentText("New Event Name:");

                // Show the dialog and wait for the user's input
                Optional<String> updateResult = updateDialog.showAndWait();

                // If the user entered a new name, update it in the database and refresh the list
                updateResult.ifPresent(newEventName -> {
                    // Create a new TextInputDialog for updating the event description
                    TextInputDialog descDialog = new TextInputDialog(selectedEvent.getEventDesc());
                    descDialog.setTitle("Update Event");
                    descDialog.setHeaderText("Enter the new event description:");
                    descDialog.setContentText("New Event Description:");

                    // Show the dialog and wait for the user's input
                    Optional<String> descResult = descDialog.showAndWait();

                    // If the user entered a new description, update it in the database and refresh the list
                    descResult.ifPresent(newEventDescription -> {
                        // Update the event in the database
                        updateEventInDatabase(selectedEvent.getId(), newEventName, newEventDescription);

                        // Refresh the event list
                        connectDisplayEventButton();
                    });
                });
            }
        });
    }


    private Event getEventByName(String eventName) {
        // Connect to the database and retrieve the event by name
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Use a prepared statement to avoid SQL injection
        String selectQuery = "SELECT * FROM eventdata WHERE event_name = ?";
        Event selectedEvent = null;

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(selectQuery);
            preparedStatement.setString(1, eventName);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if the result set has any rows
            if (resultSet.next()) {
                // Populate the selectedEvent object with data from the result set
                selectedEvent = new Event(
                        resultSet.getInt("event_id"),
                        resultSet.getString("event_name"),
                        resultSet.getString("event_desc")
                        // Add other properties as needed
                );
            }
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

        return selectedEvent;
    }

    private void updateEventInDatabase(int eventId, String newEventName, String newEventDescription) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Use a prepared statement to avoid SQL injection
        String updateQuery = "UPDATE eventdata SET event_name = ?, event_desc = ? WHERE event_id = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
            preparedStatement.setString(1, newEventName);
            preparedStatement.setString(2, newEventDescription);
            preparedStatement.setInt(3, eventId);
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
    public class Event {
        private int id;
        private String eventName;
        private String eventDesc;

        public Event(int id, String eventName, String eventDesc) {
            this.id = id;
            this.eventName = eventName;
            this.eventDesc = eventDesc;
        }

        public int getId() {
            return id;
        }

        public String getEventName() {
            return eventName;
        }

        public String getEventDesc() {
            return eventDesc;
        }
    }

    @FXML
    private void handleSearchEventButton(ActionEvent event) {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Event");
        dialog.setHeaderText("Enter the event name to search:");
        dialog.setContentText("Event Name:");

        // Show the dialog and wait for the user's input
        Optional<String> result = dialog.showAndWait();

        // If the user entered a name, search for the event in the database and display the results
        result.ifPresent(eventName -> {
            // Search for the event in the database
            List<Event> searchResults = searchEventInDatabase(eventName);

            // Clear the existing items in the ListView
            eventListView.getItems().clear();

            // Display the search results in the ListView
            if (!searchResults.isEmpty()) {
                List<String> eventNames = searchResults.stream().map(Event::getEventName).collect(Collectors.toList());
                ObservableList<String> observableList = FXCollections.observableArrayList(eventNames);
                eventListView.setItems(observableList);
            } else {
                // Handle the case when no results are found
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Search Results");
                alert.setHeaderText(null);
                alert.setContentText("No events found with the specified name.");
                alert.showAndWait();
            }
        });
    }

    private List<Event> searchEventInDatabase(String eventName) {
        // Connect to the database and retrieve events by name
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Use a prepared statement to avoid SQL injection
        String selectQuery = "SELECT * FROM eventdata WHERE event_name LIKE ?";
        List<Event> searchResults = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(selectQuery);
            preparedStatement.setString(1, "%" + eventName + "%"); // Use LIKE for partial matching
            ResultSet resultSet = preparedStatement.executeQuery();

            // Populate the searchResults list with events from the result set
            while (resultSet.next()) {
                Event event = new Event(
                        resultSet.getInt("event_id"),
                        resultSet.getString("event_name"),
                        resultSet.getString("event_desc")
                        // Add other properties as needed
                );
                searchResults.add(event);
            }
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

        return searchResults;
    }
}

