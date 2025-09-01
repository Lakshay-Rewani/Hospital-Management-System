package HospitalManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HospitalManagementGUI {
    private JFrame frame;
    private Connection connection;
    private Patient patient;
    private Doctor doctor;
    private JLabel dateTimeLabel;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a");

    public HospitalManagementGUI(Connection connection) {
        this.connection = connection;
        this.patient = new Patient(connection);
        this.doctor = new Doctor(connection);
        initialize();
    }

    private void initialize() {
        // Main frame with app-like design
        frame = new JFrame("Hospital Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Bigger for dashboard feel
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(800, 600));

        // Gradient background panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(50, 50, 70), 0, getHeight(), new Color(30, 30, 50));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(mainPanel);

        // Sidebar for navigation
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(40, 40, 60));
        sidebar.setLayout(new GridLayout(5, 1, 10, 10));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Styled buttons with icons (Unicode for now)
        JButton addPatientButton = createStyledButton("➕ Add Patient", new Color(0, 128, 0));
        JButton viewPatientsButton = createStyledButton("👁️ View Patients", new Color(0, 128, 128));
        JButton viewDoctorsButton = createStyledButton("👁️ View Doctors", new Color(0, 128, 128));
        JButton bookAppointmentButton = createStyledButton("📅 Book Appointment", new Color(128, 0, 128));
        JButton exitButton = createStyledButton("🚪 Exit", new Color(255, 0, 0));

        // Add to sidebar
        sidebar.add(addPatientButton);
        sidebar.add(viewPatientsButton);
        sidebar.add(viewDoctorsButton);
        sidebar.add(bookAppointmentButton);
        sidebar.add(exitButton);

        // Main content area with tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(50, 50, 70));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Placeholder panels for tabs (to be updated dynamically)
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setBackground(new Color(30, 30, 50));
        tabbedPane.addTab("Dashboard", dashboardPanel); // You can add dashboard content later

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Date-time label at the top
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(40, 40, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        LocalDateTime now = LocalDateTime.now();
        dateTimeLabel = new JLabel("Current Date & Time: " + now.format(DATE_TIME_FORMATTER));
        dateTimeLabel.setForeground(new Color(0, 255, 255)); // Neon cyan
        dateTimeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(dateTimeLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Action listeners
        addPatientButton.addActionListener(e -> showAddPatientDialog());
        viewPatientsButton.addActionListener(e -> showViewPatientsWindow());
        viewDoctorsButton.addActionListener(e -> showViewDoctorsWindow());
        bookAppointmentButton.addActionListener(e -> showBookAppointmentDialog());
        exitButton.addActionListener(e -> {
            frame.dispose();
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });

        frame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 120), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(bgColor.getRed() + 20, bgColor.getGreen() + 20, bgColor.getBlue() + 20));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void showAddPatientDialog() {
        JDialog dialog = new JDialog(frame, "Add Patient", true);
        dialog.setSize(400, 300);
        dialog.getContentPane().setBackground(new Color(40, 40, 60));
        dialog.setLayout(new GridLayout(4, 2, 20, 20));
        dialog.setLocationRelativeTo(frame);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        JTextField nameField = new JTextField();
        nameField.setBackground(new Color(50, 50, 70));
        nameField.setForeground(Color.WHITE);
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setForeground(Color.WHITE);
        JTextField ageField = new JTextField();
        ageField.setBackground(new Color(50, 50, 70));
        ageField.setForeground(Color.WHITE);
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setForeground(Color.WHITE);
        JTextField genderField = new JTextField();
        genderField.setBackground(new Color(50, 50, 70));
        genderField.setForeground(Color.WHITE);
        JButton submitButton = createStyledButton("✅ Submit", new Color(0, 128, 0));

        dialog.add(nameLabel);
        dialog.add(nameField);
        dialog.add(ageLabel);
        dialog.add(ageField);
        dialog.add(genderLabel);
        dialog.add(genderField);
        dialog.add(new JLabel());
        dialog.add(submitButton);

        submitButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = genderField.getText();

                if (name.isEmpty() || gender.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (age < 0) {
                    JOptionPane.showMessageDialog(dialog, "Age must be non-negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = patient.addPatient(name, age, gender);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Patient Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add patient.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid age.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void showViewPatientsWindow() {
        JFrame viewFrame = new JFrame("View Patients");
        viewFrame.setSize(700, 500);
        viewFrame.getContentPane().setBackground(new Color(30, 30, 50));
        viewFrame.setLocationRelativeTo(frame);

        String[] columns = {"ID", "Name", "Age", "Gender"};
        List<String[]> patients = patient.viewPatients();
        String[][] data = patients.toArray(new String[0][0]);

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        table.setBackground(new Color(50, 50, 70));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(70, 70, 90));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 2));
        viewFrame.add(scrollPane, BorderLayout.CENTER);

        viewFrame.setVisible(true);
    }

    private void showViewDoctorsWindow() {
        JFrame viewFrame = new JFrame("View Doctors");
        viewFrame.setSize(700, 500);
        viewFrame.getContentPane().setBackground(new Color(30, 30, 50));
        viewFrame.setLocationRelativeTo(frame);

        String[] columns = {"ID", "Name", "Specialization"};
        List<String[]> doctors = doctor.viewDoctors();
        String[][] data = doctors.toArray(new String[0][0]);

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        table.setBackground(new Color(50, 50, 70));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(70, 70, 90));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 2));
        viewFrame.add(scrollPane, BorderLayout.CENTER);

        viewFrame.setVisible(true);
    }

    private void showBookAppointmentDialog() {
        JDialog dialog = new JDialog(frame, "Book Appointment", true);
        dialog.setSize(400, 300);
        dialog.getContentPane().setBackground(new Color(40, 40, 60));
        dialog.setLayout(new GridLayout(4, 2, 20, 20));
        dialog.setLocationRelativeTo(frame);

        JLabel patientIdLabel = new JLabel("Patient ID:");
        patientIdLabel.setForeground(Color.WHITE);
        JTextField patientIdField = new JTextField();
        patientIdField.setBackground(new Color(50, 50, 70));
        patientIdField.setForeground(Color.WHITE);
        JLabel doctorIdLabel = new JLabel("Doctor ID:");
        doctorIdLabel.setForeground(Color.WHITE);
        JTextField doctorIdField = new JTextField();
        doctorIdField.setBackground(new Color(50, 50, 70));
        doctorIdField.setForeground(Color.WHITE);
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setForeground(Color.WHITE);
        JTextField dateField = new JTextField(LocalDate.now().format(DATE_FORMATTER));
        dateField.setBackground(new Color(50, 50, 70));
        dateField.setForeground(Color.WHITE);
        JButton submitButton = createStyledButton("📅 Book", new Color(128, 0, 128));

        dialog.add(patientIdLabel);
        dialog.add(patientIdField);
        dialog.add(doctorIdLabel);
        dialog.add(doctorIdField);
        dialog.add(dateLabel);
        dialog.add(dateField);
        dialog.add(new JLabel());
        dialog.add(submitButton);

        submitButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(patientIdField.getText());
                int doctorId = Integer.parseInt(doctorIdField.getText());
                String appointmentDate = dateField.getText();

                if (appointmentDate.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Date is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDate appointmentDateObj = LocalDate.parse(appointmentDate, DATE_FORMATTER);
                LocalDate today = LocalDate.now();
                if (appointmentDateObj.isBefore(today)) {
                    JOptionPane.showMessageDialog(dialog, "Appointment date cannot be in the past.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
                    if (checkDoctorAvailability(doctorId, appointmentDate)) {
                        String query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                            preparedStatement.setInt(1, patientId);
                            preparedStatement.setInt(2, doctorId);
                            preparedStatement.setString(3, appointmentDate);
                            int rowsAffected = preparedStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(dialog, "Appointment Booked!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                dialog.dispose();
                            } else {
                                JOptionPane.showMessageDialog(dialog, "Failed to book appointment.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Doctor not available on this date.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "Either patient or doctor doesn't exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid IDs.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private boolean checkDoctorAvailability(int doctorId, String appointmentDate) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}