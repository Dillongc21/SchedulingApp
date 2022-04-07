package com.scheduler.access.object.mysql;

import com.scheduler.access.connection.MySqlConnection;
import com.scheduler.access.object.DAO;
import com.scheduler.common.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class of the Data Access Object (DAO) interface to map the 'customers' table rows in the SQL
 * Database to front-end 'Customer' Model objects, to perform CRUD operations.
 */
public class CustomerDAO implements DAO<Customer> {

    private final MySqlConnection mySqlConnection;

    /**
     * Constructor: Creates or retrieves Singleton instance of the DB Connection.
     */
    public CustomerDAO() {
        mySqlConnection = MySqlConnection.getInstance();
    }

    /**
     * Read one row, of the given unique id, from the 'customers' table
     *
     * @param id 'Customer_ID' column value (unique identifier)
     * @return Customer instance holding row data or <em>null</em> Customer object if row was not found
     */
    @Override
    public Customer read(int id) {
        Customer customer = null;
        Connection conn = mySqlConnection.getConnection();
        try {
            final String QUERY = "SELECT * FROM customers WHERE Customer_ID=?;";
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int innerId = rs.getInt("Customer_ID");
                String name = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int divisionId = rs.getInt("Division_ID");
                customer = new Customer(innerId, name, address, postalCode, phone, createDate, createdBy,
                        lastUpdate, lastUpdatedBy, divisionId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    /**
     * Read one row, of the given unique name, from the 'customers' table
     *
     * @param name 'Customer_Name' column value (unique identifier)
     * @return Customer model object holding row data or <em>null</em> Customer object if row was not found
     */
    public Customer read(String name) {
        Customer customer = null;
        Connection conn = mySqlConnection.getConnection();
        try {
            final String QUERY = "SELECT * FROM customers WHERE Customer_Name=?;";
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Customer_ID");
                String innerName = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int divisionId = rs.getInt("Division_ID");
                customer = new Customer(id, innerName, address, postalCode, phone, createDate, createdBy,
                        lastUpdate, lastUpdatedBy, divisionId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    /**
     * Read all rows from the 'customers' table.
     *
     * @return List of Customer instances holding row data or empty ArrayList object if no rows were found.
     */
    @Override
    public List<Customer> readAll() {
        List<Customer> customers = new ArrayList<>();
        Connection conn = mySqlConnection.getConnection();
        try {
            final String QUERY = "SELECT * FROM customers;";
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int innerId = rs.getInt("Customer_ID");
                String name = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int divisionId = rs.getInt("Division_ID");
                customers.add(new Customer(innerId, name, address, postalCode, phone, createDate, createdBy,
                        lastUpdate, lastUpdatedBy, divisionId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Create a new row in the 'customers' table.
     *
     * @param customer Customer instance with required column data
     * @return Customer instance from newly created table row or null if not successful
     */
    @Override
    public Customer create(Customer customer) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY =
                "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, " +
                        "Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                        "VALUES (?, ?, ?, ?, current_timestamp(), \"script\", current_timestamp(), " +
                        "\"script\", ?);";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPostalCode());
            stmt.setString(4, customer.getPhone());
            stmt.setInt(5, customer.getDivisionId());
            stmt.executeUpdate();
            customer = read(customer.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    /**
     * Update a row in the 'customers' table.
     *
     * @param customer Customer instance with required column data
     * @return Customer instance from updated table row or <em>null</em> if row update was not successful
     */
    @Override
    public Customer update(Customer customer) {
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "UPDATE customers SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, " +
                "Division_ID=?, Last_Update=current_timestamp(), Last_Updated_By=\"script\" WHERE Customer_ID=?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPostalCode());
            stmt.setString(4, customer.getPhone());
            stmt.setInt(5, customer.getDivisionId());
            stmt.setInt(6, customer.getId());
            int successfulRows = stmt.executeUpdate();
            customer = (successfulRows == 0) ? null : read(customer.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    /**
     * Delete a row in the 'customers' table.
     *
     * @param id 'Customer_ID' column value (unique identifier)
     * @return Boolean of true if deleted successfully, false if not
     */
    @Override
    public boolean delete(int id) {
        boolean rowIsDeleted = false;
        Connection conn = mySqlConnection.getConnection();
        final String QUERY = "DELETE FROM customers WHERE Customer_ID=?;";
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
