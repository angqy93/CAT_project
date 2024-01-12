package org.example.xplorer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.value.ChangeListener;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventSearchController implements Initializable {

    @FXML
    private TableView<EventSearchModel> eventTableView;
    @FXML
    private TableColumn<EventSearchModel, Integer> eventDescTableColumn;
    @FXML
    private TableColumn<EventSearchModel, String> eventNameTableColumn;
    @FXML
    private TableColumn<EventSearchModel, String> eventLocationTableColumn;
    @FXML
    private TableColumn<EventSearchModel, String> eventTimeTableColumn;
    @FXML
    private TextField keywordTextField;

    ObservableList<EventSearchModel> eventSearchModelObservableList = FXCollections.observableArrayList();
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void SwitchToHomePage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Home.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

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
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Create1.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

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
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Get the primary screen
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();
        double screenHeight = screen.getVisualBounds().getHeight();

        // Set the scene size to match the screen size
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.show();
    }
    public void connectDisplayEventButton() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String connectQuery = "SELECT * FROM eventdata";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            eventSearchModelObservableList.clear(); // Clear the existing list

            while (queryOutput.next()) {
                String queryEventName = queryOutput.getString("event_name");
                String queryEventDesc = queryOutput.getString("event_desc");
                String queryEventLocation = queryOutput.getString("event_location");
                String queryEventTime = queryOutput.getString("event_time");

                eventSearchModelObservableList.add(new EventSearchModel(queryEventName, queryEventDesc, queryEventLocation, queryEventTime));
            }

            // Set the concatenated result text to the TableView
            eventTableView.setItems(eventSearchModelObservableList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up search functionality
        FilteredList<EventSearchModel> filteredData = new FilteredList<>(eventSearchModelObservableList, b -> true);
        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(eventSearchModel -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                return eventSearchModel.getEvent_name().toLowerCase().contains(searchKeyword) ||
                        eventSearchModel.getEvent_desc().toLowerCase().contains(searchKeyword);
            });
        });

        // Bind the filtered data to the TableView
        SortedList<EventSearchModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(eventTableView.comparatorProperty());
        eventTableView.setItems(sortedData);
    }
    @Override
    public void initialize(URL url, ResourceBundle resource) {
        connectDisplayEventButton(); // Initial data load

        // Set up columns
        eventNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("event_name"));
        eventDescTableColumn.setCellValueFactory(new PropertyValueFactory<>("event_desc"));
        eventLocationTableColumn.setCellValueFactory(new PropertyValueFactory<>("event_location"));
        eventTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("event_time"));

        // Set up TableView
        eventTableView.setItems(eventSearchModelObservableList);

        // Set up search functionality
        FilteredList<EventSearchModel> filteredData = new FilteredList<>(eventSearchModelObservableList, b -> true);
        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(eventSearchModel -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                return eventSearchModel.getEvent_name().toLowerCase().contains(searchKeyword) ||
                        eventSearchModel.getEvent_desc().toLowerCase().contains(searchKeyword);
            });
        });

        // Bind the filtered data to the TableView
        SortedList<EventSearchModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(eventTableView.comparatorProperty());
        eventTableView.setItems(sortedData);
    }

    //Add Events
    @FXML
    private void handleAddEventButton(ActionEvent event) {
        // Create TextInputDialogs for event details
        TextInputDialog nameDialog = createTextInputDialog("Enter the event name:", "Event Name:");
        TextInputDialog descDialog = createTextInputDialog("Enter the event description:", "Event Description:");
        TextInputDialog locationDialog = createTextInputDialog("Enter the event location:", "Event Location:");

        // Show the dialogs and wait for the user's input
        Optional<String> nameResult = nameDialog.showAndWait();
        if (!nameResult.isPresent()) {
            return; // Exit if the user pressed "Cancel" in the name dialog
        }

        Optional<String> descResult = descDialog.showAndWait();
        if (!descResult.isPresent()) {
            return; // Exit if the user pressed "Cancel" in the description dialog
        }

        Optional<String> locationResult = locationDialog.showAndWait();
        if (!locationResult.isPresent()) {
            return; // Exit if the user pressed "Cancel" in the location dialog
        }

        String timeResult = getValidDateTime();
        if (timeResult == null) {
            return; // Exit if the user pressed "Cancel" in the time dialog or provided an invalid datetime
        }

        // If the user entered all details, add the event to the database and update the list
        addEventToDatabase(nameResult.get(), descResult.get(), locationResult.get(), timeResult);

        // Refresh the event list
        connectDisplayEventButton();
    }

    private String getValidDateTime() {
        while (true) {
            TextInputDialog timeDialog = createTextInputDialog("Enter the event time (YYYY-MM-DD HH:mm:ss):", "Event Time:");

            // Show the dialog and wait for the user's input
            Optional<String> timeResult = timeDialog.showAndWait();

            // If the user pressed "Cancel" in the time dialog, return null to indicate cancellation
            if (!timeResult.isPresent()) {
                return null;
            }

            // Validate the datetime format using a regular expression
            if (isValidDateTimeFormat(timeResult.get())) {
                return timeResult.get(); // Return the valid datetime
            } else {
                // Display an error message and continue the loop to prompt the user again
                showErrorAlert("Invalid Datetime Format", "Please enter a valid datetime in the format YYYY-MM-DD HH:mm:ss");
            }
        }
    }

    private TextInputDialog createTextInputDialog(String headerText, String contentText) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Event");
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);
        return dialog;
    }

    private boolean isValidDateTimeFormat(String datetime) {
        // Use a regular expression to validate the datetime format (YYYY-MM-DD HH:mm:ss)
        String regex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
        return datetime.matches(regex);
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void addEventToDatabase(String name, String description, String location, String time) {
        // Connect to the database and execute an INSERT query to add the new event
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Use a prepared statement to avoid SQL injection
        String insertQuery = "INSERT INTO eventdata (event_name, event_desc, event_location, event_time) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, location);
            preparedStatement.setString(4, time);
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
    private void handleUpdateEventButton(ActionEvent event) {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Event");
        dialog.setHeaderText("Enter the event name to update:");
        dialog.setContentText("Event Name:");

        // Show the dialog and wait for the user's input
        Optional<String> result = dialog.showAndWait();

        // If the user entered a name, update the corresponding event in the database and update the list
        result.ifPresent(eventName -> {
            // Get the selected event from the observable list
            EventSearchModel selectedEvent = eventSearchModelObservableList.stream()
                    .filter(eventModel -> eventModel.getEvent_name().equals(eventName))
                    .findFirst()
                    .orElse(null);

            if (selectedEvent != null) {
                // Pre-fill existing data in the input fields
                String existingName = selectedEvent.getEvent_name();
                String existingDesc = selectedEvent.getEvent_desc();
                String existingLocation = selectedEvent.getEvent_location();
                String existingTime = selectedEvent.getEvent_time();

                // Create TextInputDialogs for event details with existing values as default
                TextInputDialog nameDialog = createTextInputDialog("Enter the new event name:", "New Event Name:", existingName);
                TextInputDialog descDialog = createTextInputDialog("Enter the new event description:", "New Event Description:", existingDesc);
                TextInputDialog locationDialog = createTextInputDialog("Enter the new event location:", "New Event Location:", existingLocation);
                TextInputDialog timeDialog = createTextInputDialog("Enter the new event time (YYYY-MM-DD HH:mm:ss):", "New Event Time:", existingTime);

                // Show the dialogs and wait for the user's input
                Optional<String> newNameResult = nameDialog.showAndWait();
                if (!newNameResult.isPresent()) {
                    return; // Exit if the user pressed "Cancel" in the name dialog
                }

                Optional<String> newDescResult = descDialog.showAndWait();
                if (!newDescResult.isPresent()) {
                    return; // Exit if the user pressed "Cancel" in the description dialog
                }

                Optional<String> newLocationResult = locationDialog.showAndWait();
                if (!newLocationResult.isPresent()) {
                    return; // Exit if the user pressed "Cancel" in the location dialog
                }

                Optional<String> newTimeResult = timeDialog.showAndWait();
                if (!newTimeResult.isPresent()) {
                    return; // Exit if the user pressed "Cancel" in the time dialog or provided an invalid datetime
                }

                // Update the event in the database and update the observable list
                updateEventInDatabase(selectedEvent, newNameResult.get(), newDescResult.get(), newLocationResult.get(), newTimeResult.get());

                // Refresh the event list
                connectDisplayEventButton();
            } else {
                // Event not found
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Update Result");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("No event found with the specified name.");
                errorAlert.showAndWait();
            }
        });
    }

    private TextInputDialog createTextInputDialog(String headerText, String contentText, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle("Update Event");
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);
        return dialog;
    }

    private void updateEventInDatabase(EventSearchModel existingEvent, String newName, String newDesc, String newLocation, String newTime) {
        // Connect to the database and execute an UPDATE query to update the event
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Use a prepared statement to avoid SQL injection
        String updateQuery = "UPDATE eventdata SET event_name = ?, event_desc = ?, event_location = ?, event_time = ? WHERE event_name = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newDesc);
            preparedStatement.setString(3, newLocation);
            preparedStatement.setString(4, newTime);
            preparedStatement.setString(5, existingEvent.getEvent_name());
            preparedStatement.executeUpdate();

            // Update the existing event in the observable list
            existingEvent.setEvent_name(newName);
            existingEvent.setEvent_desc(newDesc);
            existingEvent.setEvent_location(newLocation);
            existingEvent.setEvent_time(newTime);

            // Commit changes to the database
            connectDB.commit();

            // Show a success alert
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Update Result");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Event updated successfully.");
            successAlert.showAndWait();
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
        private String eventLocation;
        private String eventTime;

        // Constructor for events without location and time (using default values)
        public Event(int id, String eventName, String eventDesc) {
            this(id, eventName, eventDesc, "Default Location", "Default Time");
        }

        // Full constructor for events with all details
        public Event(int id, String eventName, String eventDesc, String eventLocation, String eventTime) {
            this.id = id;
            this.eventName = eventName;
            this.eventDesc = eventDesc;
            this.eventLocation = eventLocation;
            this.eventTime = eventTime;
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

        public String getEventLocation() {
            return eventLocation;
        }

        public String getEventTime() {
            return eventTime;
        }
    }

    // Delete Events
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
            int rowsAffected = preparedStatement.executeUpdate();

            // Check if any rows were affected (event deleted)
            if (rowsAffected > 0) {
                // Remove the deleted event from the observable list
                EventSearchModel eventToRemove = eventSearchModelObservableList.stream()
                        .filter(event -> event.getEvent_name().equals(eventName))
                        .findFirst()
                        .orElse(null);

                if (eventToRemove != null) {
                    eventSearchModelObservableList.remove(eventToRemove);
                }

                // Event successfully deleted
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Deletion Result");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Event '" + eventName + "' successfully deleted.");
                successAlert.showAndWait();
            } else {
                // Event not found
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Deletion Result");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("No event found with the specified name.");
                errorAlert.showAndWait();
            }

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
