package com.scheduler.access.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for connecting to a MySql database and storing the connection. Uses the JDBC API.
 */
public class MySqlConnection {
    private static volatile MySqlConnection instance = null;
    private Connection connection;

    /**
     * Creates an instance of the class if it has not yet been and then retrieves the one instance of 'MySqlConnection',
     * thereafter (Singleton). Thread safe implementation.
     *
     * @return the one instance of 'MySqlConnection'
     */
    public static MySqlConnection getInstance() {
        if (instance == null) {
            synchronized (MySqlConnection.class) {
                if (instance == null) {
                    instance = new MySqlConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Constructor; Makes and stores the connection to the database. Connection string credentials are stored in and
     * retrieved from environment variables.
     */
    private MySqlConnection() {
        connection = null;
        String port = System.getenv("MYSQL_SCHEDULER_DB_PORT");
        String username = System.getenv("MYSQL_SCHEDULER_DB_USERNAME");
        String password = System.getenv("MYSQL_SCHEDULER_DB_PASSWORD");
        String url = "jdbc:mysql://localhost:" + port + "/client_schedule";
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the connection to the MySql database.
     * @return SQL Connection (JDBC)
     */
    public Connection getConnection() { return connection; }
}
