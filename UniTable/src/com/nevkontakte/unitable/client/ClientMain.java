package com.nevkontakte.unitable.client;

import javax.swing.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

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
		Locale.setDefault(Locale.ENGLISH);

		/*
		ConnectionDialog connect = new ConnectionDialog("jdbc:mysql://localhost/unitable");
		connect.setVisible(true);
		if(connect.getStatus() == ConnectionDialog.Status.CONNECTED) {
			MainFrame mainWindow = new MainFrame(connect.getConnection());
			mainWindow.setVisible(true);
		}
		*/
		MainFrame mainWindow = new MainFrame(DriverManager.getConnection("jdbc:mysql://localhost/unitable", "root", "mpwd2007"));
		mainWindow.setVisible(true);
	}
}
