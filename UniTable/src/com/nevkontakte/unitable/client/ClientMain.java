package com.nevkontakte.unitable.client;

import javax.swing.*;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 10.05.11
 * Time: 22:46
 */
public class ClientMain {
	public static void main(String[] args) {
		// Set up look'n'feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		Locale.setDefault(Locale.ENGLISH);

		ConnectionDialog connect = new ConnectionDialog("jdbc:mysql://localhost/unitable");
		connect.setVisible(true);
		System.out.println(connect.getConnection());
	}
}
