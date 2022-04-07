package com.scheduler.access.object.mysql;

import com.scheduler.access.connection.MySqlConnection;
import com.scheduler.access.object.DAO;
import com.scheduler.common.model.Division;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class of the Data Access Object (DAO) interface to map the 'first_level_divisions' table rows in the
 * SQL Database to front-end 'Division' Model objects, to perform CRUD operations.
 */
public class DivisionDAO implements DAO<Division> {

    private final MySqlConnection mySqlConnection;

    /**
     * Constructor: Creates or retrieves Singleton instance of the DB Connection.
     */
    public DivisionDAO() {
        mySqlConnection = MySqlConnection.getInstance();
    }

    /**
     * Read one row, of the given unique id, from the 'first_level_divisions' table.
     *
     * @param id 'Division_ID' column value (unique identifier)
     * @return Division instance holding row data or <em>null</em> Division object if row was not found
     */
    @Override
    public Division read(int id) {
        Division division = null;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM first_level_divisions WHERE Division_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int innerId = rs.getInt("Division_ID");
                String name = rs.getString("Division");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int countryId = rs.getInt("Country_ID");
                division = new Division(innerId, name, createDate, createdBy, lastUpdate, lastUpdatedBy,
                        countryId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return division;
    }

    /**
     * Read one row, of the given unique name, from the 'first_level_divisions' table.
     *
     * @param name 'Division' column value (unique identifier)
     * @return Division instance holding row data or <em>null</em> Division object if row was not found
     */
    public Division read(String name) {
        Division division = null;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM first_level_divisions WHERE Division=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Division_ID");
                String innerName = rs.getString("Division");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int countryId = rs.getInt("Country_ID");
                division = new Division(id, innerName, createDate, createdBy, lastUpdate, lastUpdatedBy,
                        countryId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return division;
    }

    /**
     * Read all rows into Division model objects from the 'first_level_divisions' table.
     *
     * @return List of Division instances holding row data or empty ArrayList object if no rows were found.
     */
    @Override
    public List<Division> readAll() {
        List<Division> divisions = new ArrayList<>();
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM first_level_divisions;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int innerId = rs.getInt("Division_ID");
                String name = rs.getString("Division");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int countryId = rs.getInt("Country_ID");
                divisions.add(new Division(innerId, name, createDate, createdBy, lastUpdate, lastUpdatedBy,
                        countryId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisions;
    }

    /**
     * Create a new row in the 'first_level_divisions' table.
     *
     * @param division Division instance with required column data
     * @return Division instance from newly created table row or <em>null</em> if not successful
     */
    @Override
    public Division create(Division division) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY =
                "INSERT INTO first_level_divisions (Division, Create_Date, Created_By, Last_Update, Last_Updated_By, " +
                        "Country_ID) " +
                        "VALUES (?, current_timestamp(), \"script\", current_timestamp(), \"script\", ?);";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, division.getName());
            stmt.setInt(2, division.getCountryId());
            stmt.executeUpdate();
            division = read(division.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return division;
    }

    /**
     * Update a row in the 'first_level_divisions' table.
     *
     * @param division Division instance with required column data
     * @return Division instance from updated table row or <em>null</em> if row update was not successful
     */
    @Override
    public Division update(Division division) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "UPDATE first_level_divisions SET Division=?, Country_ID=?, " +
                "Last_Update=current_timestamp(), Last_Updated_By=\"script\" WHERE Division_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, division.getName());
            stmt.setInt(2, division.getCountryId());
            stmt.setInt(3, division.getId());
            int successfulRows = stmt.executeUpdate();
            division = (successfulRows == 0) ? null : read(division.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return division;
    }

    /**
     * Delete a row in the 'first_level_divisions' table.
     *
     * @param id 'Division_ID' column value (unique identifier)
     * @return Boolean of true if deleted successfully, false if not
     */
    @Override
    public boolean delete(int id) {
        boolean rowIsDeleted = false;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "DELETE FROM divisions WHERE Division_ID=?;";
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
