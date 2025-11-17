package com.b2z.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class DBManager {

    private static DBManager instance;

    private ResourceBundle properties;

    private static final String resourceBundle = "config";

    private Connection conn;

    private DBManager() {
        properties = ResourceBundle.getBundle(resourceBundle);

        try {
            Class.forName(properties.getString("DB_DRIVER"));
            this.getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static DBManager getInstance() {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(
                    properties.getString("JDBC_URL"),
                    properties.getString("DB_LOGIN"),
                    properties.getString("DB_PASSWORD")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return conn;
    }

    public void cleanup(Connection connection, Statement stat, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Permet de tester la connexion à la DB
     */
    public static void main(String[] args) {
        Connection c = DBManager.getInstance().getConnection();
        if (c != null) {
            try {
                System.out.println("Connection to db : " + c.getCatalog());
                System.out.println("Tables List :");
                ResultSet rs = c.getMetaData().getTables(c.getCatalog(), null, "%", new String[] { "TABLE" });
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    System.out.println(tableName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
