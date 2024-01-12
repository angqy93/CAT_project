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
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class testController{

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void SwitchToHomePage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main.fxml")));
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
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Create.fxml")));
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

//        initializeEventSearch();
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

    //Update Events
    @FXML
    private void handleUpdateEventButton(ActionEvent event) {
        // Create TextInputDialogs for event details
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Update Event");
        nameDialog.setHeaderText("Enter the current event name to update:");
        nameDialog.setContentText("Event Name:");

        // Show the dialog and wait for the user's input
        Optional<String> nameResult = nameDialog.showAndWait();

        // If the user pressed "Cancel" in the name dialog, exit
        if (!nameResult.isPresent()) {
            return;
        }

        // Get the selected event from the ListView
        Event selectedEvent = getEventByName(nameResult.get());

        if (selectedEvent != null) {
            // Create a new TextInputDialog with the current event name as the default value
            TextInputDialog updateDialog = new TextInputDialog(selectedEvent.getEventName());
            updateDialog.setTitle("Update Event");
            updateDialog.setHeaderText("Enter the new event name:");
            updateDialog.setContentText("New Event Name:");

            // Show the dialog and wait for the user's input
            Optional<String> updateResult = updateDialog.showAndWait();

            // If the user pressed "Cancel" in the update dialog, exit
            if (!updateResult.isPresent()) {
                return;
            }

            // Create a new TextInputDialog for updating the event description
            TextInputDialog descDialog = new TextInputDialog(selectedEvent.getEventDesc());
            descDialog.setTitle("Update Event");
            descDialog.setHeaderText("Enter the new event description:");
            descDialog.setContentText("New Event Description:");

            // Show the dialog and wait for the user's input
            Optional<String> descResult = descDialog.showAndWait();

            // If the user pressed "Cancel" in the description dialog, exit
            if (!descResult.isPresent()) {
                return;
            }

            // Create a new TextInputDialog for updating the event location
            TextInputDialog locationDialog = new TextInputDialog(selectedEvent.getEventLocation());
            locationDialog.setTitle("Update Event");
            locationDialog.setHeaderText("Enter the new event location:");
            locationDialog.setContentText("New Event Location:");

            // Show the dialog and wait for the user's input
            Optional<String> locationResult = locationDialog.showAndWait();

            // If the user pressed "Cancel" in the location dialog, exit
            if (!locationResult.isPresent()) {
                return;
            }

            // Create a new TextInputDialog for updating the event time
            TextInputDialog timeDialog = new TextInputDialog(selectedEvent.getEventTime());
            timeDialog.setTitle("Update Event");
            timeDialog.setHeaderText("Enter the new event time:");
            timeDialog.setContentText("New Event Time (YYYY-MM-DD HH:mm:ss):");

            // Show the dialog and wait for the user's input
            Optional<String> timeResult = timeDialog.showAndWait();

            // If the user pressed "Cancel" in the time dialog, exit
            if (!timeResult.isPresent()) {
                return;
            }

            // Validate the entered datetime format
            String validatedTime = getValidDateTime(timeResult.get());

            if (validatedTime == null) {
                // Prompt the user to enter a valid datetime and exit
                showAlert("Invalid Datetime", "Please enter a valid datetime format (YYYY-MM-DD HH:mm:ss).");
                return;
            }

            // Update the event in the database
            updateEventInDatabase(selectedEvent.getId(), updateResult.get(), descResult.get(), locationResult.get(), validatedTime);

            // Refresh the event list
            connectDisplayEventButton();
        }
    }

    private String getValidDateTime(String inputDateTime) {
        try {
            // Try parsing the input datetime
            LocalDateTime.parse(inputDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return inputDateTime;
        } catch (DateTimeParseException e) {
            // Return null if parsing fails
            return null;
        }
    }

    private void showAlert(String title, String content) {
        // Create and show an alert with the specified title and content
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateEventInDatabase(int eventId, String newEventName, String newEventDescription, String newEventLocation, String newEventTime) {
        // Connect to the database and execute an UPDATE query to update the event
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Use a prepared statement to avoid SQL injection
        String updateQuery = "UPDATE eventdata SET event_name = ?, event_desc = ?, event_location = ?, event_time = ? WHERE event_id = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
            preparedStatement.setString(1, newEventName);
            preparedStatement.setString(2, newEventDescription);
            preparedStatement.setString(3, newEventLocation);
            preparedStatement.setString(4, newEventTime);
            preparedStatement.setInt(5, eventId);
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

    //Search Event
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
                showAlert("Search Results", "No events found with the specified name.");

                // Refresh the event list
                connectDisplayEventButton();
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

//    @FXML
//    public void initializeJoinPage() {
//        // Call connectDisplayEventButton to initialize the data when the page is loaded
//        connectDisplayEventButton();
//
//        // Initialize the event search functionality
//        initializeEventSearch(eventTableView);
//    }
//    @FXML
//    private TableView<EventSearchModel> eventTableView;
//    @FXML
//    private TableColumn<EventSearchModel, Integer> eventDescTableColumn;
//    @FXML
//    private TableColumn<EventSearchModel, String> eventNameTableColumn;
//    @FXML
//    private TableColumn<EventSearchModel, String> eventLocationTableColumn;
//    @FXML
//    private TableColumn<EventSearchModel, String> eventTimeTableColumn;
//    @FXML
//    private TextField keywordTextField;
//    ObservableList<EventSearchModel> eventSearchModelObservableList = FXCollections.observableArrayList();
//    private void initializeEventSearch(TableView<EventSearchModel> eventTableView) {
//            DatabaseConnection connectNow = new DatabaseConnection();
//            Connection connectDB = connectNow.getConnection();
//
//            String eventViewQuery = "SELECT event_name, event_desc, event_location, event_time FROM eventdata";
//
//            try{
//                Statement statement = connectDB.createStatement();
//                ResultSet queryOutput = statement.executeQuery(eventViewQuery);
//
//                while (queryOutput.next()) {
//
//                    String queryEventName = queryOutput.getString("event_name");
//                    String queryEventDesc = queryOutput.getString("event_desc");
//                    String queryEventLocation = queryOutput.getString("event_location");
//                    String queryEventTime = queryOutput.getString("event_time");
//
//
//                    eventSearchModelObservableList.add(new EventSearchModel(queryEventName,queryEventDesc,queryEventLocation,queryEventTime));
//                }
//
//                eventNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("event_name"));
//                eventDescTableColumn.setCellValueFactory(new PropertyValueFactory<>("event_desc"));
//                eventLocationTableColumn.setCellValueFactory(new PropertyValueFactory<>("event_location"));
//                eventTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("event_time"));
//
//                this.eventTableView.setItems(eventSearchModelObservableList);
//
//                FilteredList<EventSearchModel> filteredData = new FilteredList<>(eventSearchModelObservableList, b -> true);
//
//                keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//                    filteredData.setPredicate(eventSearchModel -> {
//                        if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
//                            return true;
//                        }
//
//                        String searchKeyword = newValue.toLowerCase();
//
//                        if (eventSearchModel.getEvent_name().toLowerCase().contains(searchKeyword)) {
//                            return true; // Means we found a match in Event Name
//                        } else if (eventSearchModel.getEvent_desc().toLowerCase().contains(searchKeyword)) {
//                            return true; // Means we found a match in Description
//                        } else return false; // No match found
//                    });
//                });
//
//                SortedList<EventSearchModel> sortedData = new SortedList<>(filteredData);
//
//                sortedData.comparatorProperty().bind(this.eventTableView.comparatorProperty());
//
//                this.eventTableView.setItems(sortedData);
//
//            } catch(SQLException e) {
//
//                Logger.getLogger(EventSearchController.class.getName()).log(Level.SEVERE, null, e);
//                e.printStackTrace();
//            }
//        }
//
//    public void initializeJoinPage(URL url, ResourceBundle resource) {
//        connectDisplayEventButton();
//        initializeEventSearch(eventTableView);
//    }
//}
