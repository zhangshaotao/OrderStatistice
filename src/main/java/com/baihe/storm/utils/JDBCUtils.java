package com.baihe.storm.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtils {
	public static Connection getConnection() throws ClassNotFoundException, SQLException
	{
		Connection connection = null;
		
		Class.forName(Constant.MYSQL_DRIVER);
		connection = DriverManager.getConnection(Constant.MYSQL_URL,Constant.MYSQL_USERNAME,Constant.MYSQL_PASSWORD);
		connection.setAutoCommit(false);
		
		return connection;
	}
}
