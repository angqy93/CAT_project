package org.example.xplorer;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public Connection databaseLink;

    public Connection getConnection(){
        String databaseUser = "root";
        String databasePassword = "123456";
        String url = "jdbc:mysql://127.0.0.1:3306/event_schema?useSSL=false&serverTimezone=UTC";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return databaseLink;
    }
}
