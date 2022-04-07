package com.scheduler.access.object.mysql;

import com.scheduler.access.connection.MySqlConnection;
import com.scheduler.access.object.DAO;
import com.scheduler.common.model.Country;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class of the Data Access Object (DAO) interface to map the 'countries' table rows in the SQL
 * Database to front-end 'Country' Model objects, to perform CRUD operations.
 */
public class CountryDAO implements DAO<Country> {

    private final MySqlConnection mySqlConnection;

    /**
     * Constructor: Creates or retrieves Singleton instance of the DB Connection.
     */
    public CountryDAO() {
        mySqlConnection = MySqlConnection.getInstance();
    }

    /**
     * Read one row, of the given unique id, from the 'countries' table
     *
     * @param id 'Country_ID' column value (unique identifier)
     * @return Country instance holding row data or <em>null</em> Country object if row was not found
     */
    @Override
    public Country read(int id) {
        Country country = null;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM countries WHERE Country_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int innerId = rs.getInt("Country_ID");
                String name = rs.getString("Country");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                country = new Country(innerId, name, createDate, createdBy, lastUpdate, lastUpdatedBy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return country;
    }

    /**
     * Read one row, of the given unique name, from the 'countries' table
     *
     * @param name 'Country' column value (unique identifier)
     * @return Country instance holding row data or <em>null</em> Country object if row was not found
     */
    public Country read(String name) {
        Country country = null;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM countries WHERE Country=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Country_ID");
                String innerName = rs.getString("Country");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                country = new Country(id, innerName, createDate, createdBy, lastUpdate, lastUpdatedBy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return country;
    }

    /**
     * Read all rows from the 'countries' table into Country instances.
     *
     * @return List of Country model objects holding row data or empty ArrayList object if no rows were found.
     */
    @Override
    public List<Country> readAll() {
        List<Country> countries = new ArrayList<>();
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "SELECT * FROM countries;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Country_ID");
                String name = rs.getString("Country");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                countries.add(new Country(id, name, createDate, createdBy, lastUpdate, lastUpdatedBy));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countries;
    }

    /**
     * Create a new row in the 'countries' table.
     *
     * @param country Country instance with required column data
     * @return Country instance from newly created table row or <em>null</em> if row creation was not successful
     */
    @Override
    public Country create(Country country) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY =
                "INSERT INTO first_level_divisions (Country, Create_Date, Created_By, Last_Update, Last_Updated_By) " +
                        "VALUES (?, current_timestamp(), \"script\", current_timestamp(), \"script\");";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, country.getName());
            stmt.executeUpdate();
            country = read(country.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return country;
    }

    /**
     * Update a row in the 'countries' table.
     *
     * @param country Country instance with required column data
     * @return Country instance from updated table row or <em>null</em> if row update was not successful
     */
    @Override
    public Country update(Country country) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "UPDATE countries SET Country=?, " +
                "Last_Update=current_timestamp(), Last_Updated_By=\"script\" WHERE Country_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, country.getName());
            stmt.setInt(2, country.getId());
            int successfulRows = stmt.executeUpdate();
            country = (successfulRows == 0) ? null : read(country.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return country;
    }

    /**
     * Delete a row in the 'countries' table.
     *
     * @param id 'Country_ID' column value (unique identifier)
     * @return Boolean of true if deleted successfully, false if not
     */
    @Override
    public boolean delete(int id) {
        boolean rowIsDeleted = false;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "DELETE FROM countries WHERE Country_ID=?;";
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
