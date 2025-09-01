package HospitalManagementSystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Doctor {
    private Connection connection;

    public Doctor(Connection connection) {
        this.connection = connection;
        validateConnection();
    }

    private void validateConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database connection is null. Cannot proceed.");
        }
        try {
            if (connection.isClosed()) {
                throw new IllegalStateException("Database connection is closed. Cannot proceed.");
            }
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "doctors", null);
            if (!tables.next()) {
                throw new IllegalStateException("The 'doctors' table does not exist in the database.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to validate database connection: " + e.getMessage(), e);
        }
    }

    public List<String[]> viewDoctors() {
        List<String[]> doctors = new ArrayList<>();
        String query = "SELECT * FROM doctors";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String[] doctor = new String[3];
                doctor[0] = String.valueOf(resultSet.getInt("id"));
                doctor[1] = resultSet.getString("name");
                doctor[2] = resultSet.getString("specialization");
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    public boolean getDoctorById(int id) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}