package br.com.compass.db;

import br.com.compass.db.exception.DbException;

import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class Database {

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Properties properties = loadProperties();
                String url = properties.getProperty("dburl");
                String username = properties.getProperty("user");
                String password = properties.getProperty("password");

                connection = DriverManager.getConnection(url, username, password);

            } catch (SQLException | IOException exception) {
                throw new DbException(exception.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream("db.properties")) {
            properties.load(fis);
        }
        return properties;
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }
}
