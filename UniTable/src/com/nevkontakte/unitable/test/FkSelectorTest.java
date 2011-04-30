package com.nevkontakte.unitable.test;

import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.TableModel;
import com.nevkontakte.unitable.view.UnitableFkSelector;
import oracle.jdbc.OracleDriver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 29.04.11
 * Time: 19:16
 */
public class FkSelectorTest {
	public static void main(String[] args) throws SQLException {
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
		Connection connection = DriverManager.getConnection(dburl, user, password);
		TableModel model = TableModel.get(connection, "faculty");
		System.out.println(model);
		TableData data = new TableData(model);

		// Show GUI
		JFrame f = new JFrame("Test table");
		final JLabel value = new JLabel();
		final UnitableFkSelector s = new UnitableFkSelector(data);
		s.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//value.setText(s.getSelectedItem().toString()+" "+s.getSelectedItem().getClass().getName());
				//value.repaint();
			}
		});
		f.add(s, BorderLayout.NORTH);
		f.add(value);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setMinimumSize(new Dimension(400, 300));
		f.pack();
		f.setVisible(true);

	}
}
