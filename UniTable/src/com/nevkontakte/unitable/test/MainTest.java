package com.nevkontakte.unitable.test;

import com.nevkontakte.unitable.model.TableModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * User: aleks
 * Date: 25.03.11
 * Time: 18:08
 */
public class MainTest {
	public static void main(String[] args) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/unitable", "root", "mpwd2007");
		TableModel model = new TableModel(connection, "users");
	}
}
