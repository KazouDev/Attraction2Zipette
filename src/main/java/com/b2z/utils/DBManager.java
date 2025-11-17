package com.b2z.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class DBManager {

	private static DBManager instance;

	private ResourceBundle properties;

	private static String resourceBundle = "config";

	private Connection conn;

	private DBManager() {
		properties = ResourceBundle.getBundle(resourceBundle);

		try {
			Class.forName(properties.getString("DB_DRIVER"));
			this.getConnection();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static DBManager getInstance() {
		if (instance == null) {
			synchronized (DBManager.class) {
				instance = new DBManager();
			}
		}
		return instance;
	}

	public Connection getConnection() {
		try {
			if (conn == null) {
				conn = DriverManager.getConnection(properties.getString("JDBC_URL"), properties.getString("DB_LOGIN"),
						properties.getString("DB_PASSWORD"));
			} else {
				if (conn.isClosed()) {
					conn = DriverManager.getConnection(properties.getString("JDBC_URL"), properties.getString("DB_LOGIN"),
							properties.getString("DB_PASSWORD"));
				}
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
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
