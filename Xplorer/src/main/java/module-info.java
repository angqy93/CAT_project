module org.example.xplorer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.xplorer to javafx.fxml;
    exports org.example.xplorer;
}