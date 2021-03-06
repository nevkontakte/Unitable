package com.nevkontakte.unitable.client;

import javax.swing.*;
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

		Locale.setDefault(Locale.ENGLISH);
		try{
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
		}
		try{
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
		}
		Properties props = new Properties();
		props.put("useUnicode","true");
		props.put("remarks", "true");
		props.put("characterEncoding", "utf8");

		final ConnectionDialog connect = new ConnectionDialog("jdbc:oracle:thin:@localhost:1521:XE", props);
		connect.setVisible(true);

		if(connect.getStatus() == ConnectionDialog.Status.CONNECTED) {
			final ProgressBarDialog idle = new ProgressBarDialog(connect, "Connecting to database...");
			idle.setVisible(true);
			new Thread(new Runnable() {
				public void run() {
					final MainFrame mainWindow = new MainFrame(connect.getConnection());
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							mainWindow.setVisible(true);
							idle.setVisible(false);
							idle.dispose();
						}
					});
				}
			}).run();
		}
	}
}
