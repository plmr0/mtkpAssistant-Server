package com.devplmr;

import org.jetbrains.annotations.NotNull;
import org.sqlite.JDBC;

import java.io.File;
import java.sql.*;
import java.util.List;

public class DB_Handler
{
	private static final String DB_NAME = "assistant_db.sqlite3";
	private static final String URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/SQLITE/" + DB_NAME;

	public DB_Handler() throws SQLException
	{
		DriverManager.registerDriver(new JDBC());

		File sqliteFolder = new File("SQLITE");
		if (!sqliteFolder.exists())
		{
			sqliteFolder.mkdir();
		}
		else
		{
			/* PASS */
		}
	}

	private Connection connect()
	{
		Connection conn = null;
		try
		{
			conn = DriverManager.getConnection(URL);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public static void createNewDatabase()
	{
		try (Connection conn = DriverManager.getConnection(URL))
		{
			if (conn != null)
			{
				DatabaseMetaData meta = conn.getMetaData();
			}
			else
			{
				/* PASS */
			}
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void createNewGroupTable()
	{
		String sql = "CREATE TABLE groups\n"
				+ "(\n"
				+ "id INTEGER PRIMARY KEY,\n"
				+ "group_name TEXT NOT NULL\n"
				+ ");";

		try (Connection conn = DriverManager.getConnection(URL);
		     Statement stmt = conn.createStatement())
		{
			stmt.execute(sql);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public void insertGroups(@NotNull List<String> groups)
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
