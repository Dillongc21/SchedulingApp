package com.scheduler.access.object.mysql;

import com.scheduler.access.connection.MySqlConnection;
import com.scheduler.access.object.DAO;
import com.scheduler.common.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class of the Data Access Object (DAO) interface to map the 'users' table rows in the SQL
 * Database to front-end 'User' Model objects, to perform CRUD operations.
 */
public class UserDAO implements DAO<User> {

    private final MySqlConnection mySqlConnection;

    /**
     * Constructor: Creates or retrieves Singleton instance of the DB Connection.
     */
    public UserDAO() {
        mySqlConnection = MySqlConnection.getInstance();
    }

    /**
     * Read one row, of the given unique id, from the 'users' table
     *
     * @param id 'User_ID' column value (unique identifier)
     * @return User instance holding row data or <em>null</em> User object if row was not found
     */
    @Override
    public User read(int id) {
        User user = null;
        Connection conn = mySqlConnection.getConnection();
        try {
            final String QUERY = "SELECT * FROM users WHERE User_ID=?;";
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int innerId = rs.getInt("User_ID");
                String username = rs.getString("User_Name");
                String password = rs.getString("Password");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                user = new User(innerId, username, password, createDate, createdBy, lastUpdate, lastUpdatedBy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Read one row, of the given unique username, from the 'users' table
     *
     * @param username 'User_Name' column value (unique identifier)
     * @return User instance holding row data or <em>null</em> User object if row was not found
     */
    public User read(String username) {
        User user = null;
        Connection conn = mySqlConnection.getConnection();
        try {
            final String QUERY = "SELECT * FROM users WHERE User_Name=?;";
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("User_ID");
                String innerUsername = rs.getString("User_Name");
                String password = rs.getString("Password");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                user = new User(id, innerUsername, password, createDate, createdBy, lastUpdate, lastUpdatedBy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Read all rows into User model objects from the 'users' table.
     *
     * @return List of User model objects holding row data or empty ArrayList object if no rows were found.
     */
    @Override
    public List<User> readAll() {
        List<User> users = new ArrayList<>();
        Connection conn = mySqlConnection.getConnection();
        try {
            final String QUERY = "SELECT * FROM users;";
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("User_ID");
                String username = rs.getString("User_Name");
                String password = rs.getString("Password");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                users.add(new User(id, username, password, createDate, createdBy, lastUpdate, lastUpdatedBy));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Create a new row in the 'users' table.
     *
     * @param user User instance with required column data
     * @return User instance from newly created table row or null if not successful
     */
    @Override
    public User create(User user) {
        Connection conn = mySqlConnection.getConnection();
        try {
            final String QUERY =
                    "INSERT INTO users (User_Name, Password, Create_Date, Created_By, Last_Update, Last_Updated_By) " +
                            "VALUES (?, ?, current_timestamp(), \"script\", current_timestamp(), \"script\");";
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
            user = read(user.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Update a row in the 'users' table.
     *
     * @param user User instance with required column data
     * @return User instance from updated table row or <em>null</em> if row update was not successful
     */
    @Override
    public User update(User user) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "UPDATE users SET User_Name=?, Password=?, Last_Update=current_timestamp(), " +
                "Last_Updated_By=\"script\" WHERE User_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getId());
            int successfulRows = stmt.executeUpdate();
            user = (successfulRows == 0) ? null : read(user.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Delete a row in the 'users' table.
     *
     * @param id 'User_ID' column value (unique identifier)
     * @return Boolean of true if deleted successfully, false if not
     */
    @Override
    public boolean delete(int id) {
        boolean rowIsDeleted = false;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "DELETE FROM users WHERE User_ID=?;";
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
