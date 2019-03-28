package com.devplmr;

import org.sqlite.JDBC;

import java.io.File;
import java.sql.*;
import java.util.List;

public class DB_Handler
{
	public static final String DB_NAME = "assistant_db.sqlite3";

	public DB_Handler() throws SQLException
	{
		DriverManager.registerDriver(new JDBC());

		File folder = new File("sqlite");
		if (!folder.exists()) folder.mkdir();
		else {}
	}

	private Connection connect()
	{
		String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/sqlite/" + DB_NAME;
		Connection conn = null;
		try
		{
			conn = DriverManager.getConnection(url);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public static void createNewDatabase()
	{
		String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/sqlite/" + DB_NAME;

		try (Connection conn = DriverManager.getConnection(url))
		{
			if (conn != null)
			{
				DatabaseMetaData meta = conn.getMetaData();
			}
			else {}

		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void createNewGroupTable()
	{
		String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/sqlite/" + DB_NAME;

		String sql = "CREATE TABLE groups\n"
				+ "(\n"
				+ "id INTEGER PRIMARY KEY,\n"
				+ "group_name TEXT NOT NULL\n"
				+ ");";

		try (Connection conn = DriverManager.getConnection(url);
		     Statement stmt = conn.createStatement())
		{
			stmt.execute(sql);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public void insertGroups(List<String> groups)
	{
		String sql = "INSERT INTO groups(group_name) VALUES(?)";

		try (Connection conn = this.connect();
		     PreparedStatement preparedStatement = conn.prepareStatement(sql))
		{
			for (String group_name : groups)
			{
				preparedStatement.setString(1, group_name);
				preparedStatement.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
