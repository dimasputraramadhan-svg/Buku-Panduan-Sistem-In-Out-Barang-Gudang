package com.warehouse.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    
    // Konfigurasi database - sesuaikan dengan HeidiSQL
    private static final String URL = "jdbc:mariadb://localhost:3306/warehouse_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456"; // isi password jika ada
    
    private static Connection connection = null;
    
    // Private constructor - Singleton pattern
    private DatabaseConfig() {}
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                LOGGER.info("Database connection established");
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "MariaDB Driver not found", e);
                throw new SQLException("Database driver not found", e);
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection", e);
            } finally {
                connection = null;
            }
        }
    }
    
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection test failed", e);
            return false;
        }
    }
}