package com.nevkontakte.unitable.test;

import com.nevkontakte.unitable.model.DatabaseModel;
import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.TableModel;
import oracle.jdbc.OracleDriver;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

/**
 * User: aleks
 * Date: 25.03.11
 * Time: 18:08
 */
public class MainTest {
	public static void main(String[] args) throws SQLException {
		// Set up look'n'feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		Locale.setDefault(Locale.ENGLISH);
		DriverManager.registerDriver(new OracleDriver());
		String dburl = "jdbc:oracle:thin:@localhost:1521:XE";
		dburl = "jdbc:mysql://localhost/unitable";
		String user = "aleks";
		String password = "mpwd2007";
		Properties props = new Properties();
		props.put("user", user);
		props.put("password", password);
		props.put("remarksReporting", "true");
		Connection connection = DriverManager.getConnection(dburl, props);
		TableModel model = TableModel.get(connection, "teacher");
		System.out.println(model);
		TableData data = new TableData(model);
		System.out.println(new DatabaseModel(connection));

		// Show GUI
		/*
		JFrame f = new JFrame(model.getTableName());
		UnitableView table = new UnitableView(new UnitableViewModel(data));
		f.add(table);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		f.setVisible(true);
		*/
	}
}
