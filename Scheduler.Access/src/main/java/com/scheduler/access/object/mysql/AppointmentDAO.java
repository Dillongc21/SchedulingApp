package com.scheduler.access.object.mysql;

import com.scheduler.access.connection.MySqlConnection;
import com.scheduler.access.object.DAO;
import com.scheduler.common.model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class of the Data Access Object (DAO) interface to map the 'appointments' table rows in the SQL
 * Database to front-end 'Appointment' Model objects, to perform CRUD operations.
 */
public class AppointmentDAO implements DAO<Appointment> {

    private final MySqlConnection mySqlConnection;

    /**
     * Constructor: Creates or retrieves Singleton instance of the DB Connection.
     */
    public AppointmentDAO() {
        mySqlConnection = MySqlConnection.getInstance();
    }

    /**
     * Read one row, of the given unique id, from the 'appointments' table
     *
     * @param id 'Appointment_ID' column value (unique identifier)
     * @return Appointment instance holding row data or <em>null</em> Appointment object if row was not found
     */
    @Override
    public Appointment read(int id) {
        Appointment appointment = null;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM appointments WHERE Appointment_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int innerId = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                Timestamp start = rs.getTimestamp("Start");
                Timestamp end = rs.getTimestamp("End");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int customerId = rs.getInt("Customer_ID");
                int userId = rs.getInt("User_ID");
                int contactId = rs.getInt("Contact_ID");
                appointment = new Appointment(innerId, title, description, location, type, start, end, createDate,
                        createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointment;
    }

    /**
     * Read one row, of the given unique name, from the 'appointments' table
     *
     * @param title 'Title' column value (unique identifier)
     * @return Appointment instance holding row data or <em>null</em> Contacts object if row was not found
     */
    public Appointment read(String title) {
        Appointment appointment = null;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM appointments WHERE Title=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Appointment_ID");
                String innerTitle = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                Timestamp start = rs.getTimestamp("Start");
                Timestamp end = rs.getTimestamp("End");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int customerId = rs.getInt("Customer_ID");
                int userId = rs.getInt("User_ID");
                int contactId = rs.getInt("Contact_ID");
                appointment = new Appointment(id, innerTitle, description, location, type, start, end, createDate,
                        createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointment;
    }

    /**
     * Method to read all rows into Appointment model objects from the 'appointments' table.
     *
     * @return List of Contact model objects holding row data or empty ArrayList object if no rows were found.
     */
    @Override
    public List<Appointment> readAll() {
        List<Appointment> appointments = new ArrayList<>();
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM appointments;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Appointment_ID");
                String innerTitle = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                Timestamp start = rs.getTimestamp("Start");
                Timestamp end = rs.getTimestamp("End");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int customerId = rs.getInt("Customer_ID");
                int userId = rs.getInt("User_ID");
                int contactId = rs.getInt("Contact_ID");
                appointments.add(new Appointment(id, innerTitle, description, location, type, start, end, createDate,
                        createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Create a new row in the 'appointments' table.
     *
     * @param appointment Appointment instance with required column data
     * @return Appointment instance from newly created table row or <em>null</em> if row creation was not successful
     */
    @Override
    public Appointment create(Appointment appointment) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY =
                "INSERT INTO appointments (Title, Description, Location, Type, Start, End, " +
                    "Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, " +
                    "Contact_ID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, current_timestamp(), \"script\", current_timestamp(), \"script\"," +
                        "?, ?, ?);";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, appointment.getTitle());
            stmt.setString(2, appointment.getDescription());
            stmt.setString(3, appointment.getLocation());
            stmt.setString(4, appointment.getType());
            stmt.setTimestamp(5, appointment.getStart());
            stmt.setTimestamp(6, appointment.getEnd());
            stmt.setInt(7, appointment.getCustomerId());
            stmt.setInt(8, appointment.getUserId());
            stmt.setInt(9, appointment.getContactId());
            stmt.executeUpdate();
            appointment = read(appointment.getTitle());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointment;
    }

    /**
     * Update a row in the 'appointments' table.
     *
     * @param appointment Appointment instance with required column data
     * @return Appointment instance from updated table row or <em>null</em> if row update was not successful
     */
    @Override
    public Appointment update(Appointment appointment) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY =
                "UPDATE appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, " +
                    "End=?, Last_Update=current_timestamp(), Last_Updated_By=\"script\", Customer_ID=?, " +
                    "User_ID=?, Contact_ID=? WHERE Appointment_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, appointment.getTitle());
            stmt.setString(2, appointment.getDescription());
            stmt.setString(3, appointment.getLocation());
            stmt.setString(4, appointment.getType());
            stmt.setTimestamp(5, appointment.getStart());
            stmt.setTimestamp(6, appointment.getEnd());
            stmt.setInt(7, appointment.getCustomerId());
            stmt.setInt(8, appointment.getUserId());
            stmt.setInt(9, appointment.getContactId());
            stmt.setInt(10, appointment.getId());
            int successfulRows = stmt.executeUpdate();
            appointment = (successfulRows == 0) ? null : read(appointment.getTitle());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointment;
    }

    /**
     * Delete a row in the 'appointments' table.
     *
     * @param id 'Appointment_ID' column value (unique identifier)
     * @return Boolean of true if deleted successfully, false if not
     */
    @Override
    public boolean delete(int id) {
        boolean rowIsDeleted = false;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "DELETE FROM appointments WHERE Appointment_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setInt(1, id);
            rowIsDeleted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowIsDeleted;
    }
}
