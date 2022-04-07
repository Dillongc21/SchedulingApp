package com.scheduler.access.object.mysql;

import com.scheduler.access.connection.MySqlConnection;
import com.scheduler.access.object.DAO;
import com.scheduler.common.model.Contact;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class of the Data Access Object (DAO) interface to map the 'contacts' table rows in the SQL
 * Database to front-end 'Contact' Model objects, to perform CRUD operations.
 */
public class ContactDAO implements DAO<Contact> {

    private final MySqlConnection mySqlConnection;

    /**
     * Constructor: Creates or retrieves Singleton instance of the DB Connection.
     */
    public ContactDAO() {
        mySqlConnection = MySqlConnection.getInstance();
    }

    /**
     * Read one row, of the given unique id, from the 'contacts' table.
     *
     * @param id 'Contact_ID' column value (unique identifier)
     * @return Contact instance holding row data or <em>null</em> Contact object if row was not found
     */
    @Override
    public Contact read(int id) {
        Contact contact = null;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM contacts WHERE Contact_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int innerId = rs.getInt("Contact_ID");
                String name = rs.getString("Contact_Name");
                String email = rs.getString("Email");
                contact = new Contact(innerId, name, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contact;
    }

    /**
     * Read one row, of the given unique name, from the 'contacts' table
     *
     * @param name 'Contact_Name' column value (unique identifier)
     * @return Contact instance holding row data or <em>null</em> Contact object if row was not found
     */
    public Contact read(String name) {
        Contact contact = null;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM contacts WHERE Contact_Name=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Contact_ID");
                String innerName = rs.getString("Contact_Name");
                String email = rs.getString("Email");
                contact = new Contact(id, innerName, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contact;
    }

    /**
     * Method to read all rows into Contact model objects from the 'contacts' table.
     *
     * @return List of Contact model objects holding row data or empty ArrayList object if no rows were found.
     */
    @Override
    public List<Contact> readAll() {
        List<Contact> contacts = new ArrayList<>();
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM contacts;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int innerId = rs.getInt("Contact_ID");
                String name = rs.getString("Contact_Name");
                String email = rs.getString("Email");
                contacts.add(new Contact(innerId, name, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * Create a new row in the 'contacts' table.
     *
     * @param contact Contact instance with required column data
     * @return Contact instance from newly created table row or <em>null</em> if row creation was not successful
     */
    @Override
    public Contact create(Contact contact) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "INSERT INTO contacts (Contact_Name, Email) VALUES (?, ?);";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, contact.getName());
            stmt.executeUpdate();
            contact = read(contact.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contact;
    }

    /**
     * Update a row in the 'contacts' table.
     *
     * @param contact Contact instance with required column data
     * @return Contact instance from updated table row or <em>null</em> if row update was not successful
     */
    @Override
    public Contact update(Contact contact) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "UPDATE contacts SET Contact_Name=?, Email=? WHERE Contact_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, contact.getName());
            stmt.setString(2, contact.getEmail());
            stmt.setInt(3, contact.getId());
            int successfulRows = stmt.executeUpdate();
            contact = (successfulRows == 0) ? null : read(contact.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contact;
    }

    /**
     * Delete a row in the 'contacts' table.
     *
     * @param id 'Contact_ID' column value (unique identifier)
     * @return Boolean of true if deleted successfully, false if not
     */
    @Override
    public boolean delete(int id) {
        boolean rowIsDeleted = false;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "DELETE FROM contacts WHERE Contact_ID=?;";
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
