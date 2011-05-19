package com.nevkontakte.unitable.client;

import oracle.jdbc.OracleDriver;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 10.05.11
 * Time: 22:46
 */
public class ClientMain {
	public static void main(String[] args) throws SQLException {
		// Set up look'n'feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		Locale.setDefault(new Locale("RUSSIAN"));

		/*
		ConnectionDialog connect = new ConnectionDialog("jdbc:mysql://localhost/unitable");
		connect.setVisible(true);
		if(connect.getStatus() == ConnectionDialog.Status.CONNECTED) {
			MainFrame mainWindow = new MainFrame(connect.getConnection());
			mainWindow.setVisible(true);
		}
		*/
		Locale.setDefault(Locale.ENGLISH);
		DriverManager.registerDriver(new OracleDriver());
		Properties props = new Properties();
		props.put("user", "unitable");
		props.put("password", "mpwd2007");
		props.put("useUnicode","true");
		props.put("remarks", "true");
		props.put("characterEncoding", "utf8");
		Connection db = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", props);
		//Connection db = DriverManager.getConnection("jdbc:mysql://localhost/unitable", props);
		MainFrame mainWindow = new MainFrame(db);
		mainWindow.setVisible(true);
	}
}
