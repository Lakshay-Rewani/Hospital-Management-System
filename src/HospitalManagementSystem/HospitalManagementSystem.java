package HospitalManagementSystem;

import java.sql.*;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "toor";

    public static void main(String[] args) {
        Connection connection = null;
        try {
            System.out.println("Attempting to load MySQL driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully. Attempting to connect...");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Successfully connected to the database.");

            // Start the GUI
            new HospitalManagementGUI(connection);

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}