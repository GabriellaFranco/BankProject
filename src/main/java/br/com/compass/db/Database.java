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

            } catch (SQLException | IOException exc) {
                throw new DbException(exc.getMessage(), exc);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException exc) {
                throw new DbException(exc.getMessage(), exc);
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
            } catch (SQLException exc) {
                throw new DbException(exc.getMessage(), exc);
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException exc) {
                throw new DbException(exc.getMessage(), exc);
            }
        }
    }
}
