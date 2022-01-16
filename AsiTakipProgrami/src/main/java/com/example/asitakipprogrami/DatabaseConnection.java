package com.example.asitakipprogrami;

import java.sql.*;

public class DatabaseConnection {

    private Statement statement;

    public Statement getStatement(){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/asi_takip_programi", "root", "Zeynep12345*");
            statement = connection.createStatement();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return statement;
    }
}
