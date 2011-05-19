package com.nevkontakte.unitable.test;

import com.nevkontakte.unitable.client.MainFrame;
import com.nevkontakte.unitable.client.ProgressBarDialog;
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
 * Date: 19.05.11
 * Time: 22:45
 */
public class ClientTestMain {
	public static void main(String[] args) throws SQLException {
		// Set up look'n'feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		final ProgressBarDialog idle = new ProgressBarDialog(null, "Connecting to database...");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				idle.setVisible(true);
			}
		});

		Locale.setDefault(Locale.ENGLISH);
		DriverManager.registerDriver(new OracleDriver());
		Properties props = new Properties();
		props.put("useUnicode","true");
		props.put("remarks", "true");
		props.put("characterEncoding", "utf8");

		// Quick start for debugging
		props.put("user", "unitable");
		props.put("password", "mpwd2007");
		Connection db = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", props);
		//Connection db = DriverManager.getConnection("jdbc:mysql://localhost/unitable", props);
		MainFrame mainWindow = new MainFrame(db);
		mainWindow.setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				idle.setVisible(false);
			}
		});
	}

}
