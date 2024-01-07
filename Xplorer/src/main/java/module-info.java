module org.example.xplorer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.xplorer to javafx.fxml;
    exports org.example.xplorer;
}