package com.nevkontakte.unitable.client;

import com.nevkontakte.unitable.foreign.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 10.05.11
 * Time: 22:46
 */
public class ConnectionDialog extends JDialog{
	private static final int FIELD_PADDING = 5;
	
	private JTextField login = new JTextField();
	private JPasswordField password = new JPasswordField();
	private Status status = Status.EXIT;
	private Connection connection = null;
	private String url;
	private Properties props;

	public ConnectionDialog(String url, Properties props) {
		this.url = url;
		this.props = props;
		this.login.setColumns(15);
		this.password.setColumns(15);
		KeyAdapter submitter = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					onOk();
				} else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					onExit();
				}
			}
		};
		this.login.addKeyListener(submitter);
		this.password.addKeyListener(submitter);

		JPanel mainPanel = new JPanel(new SpringLayout());
		mainPanel.add(new JLabel("Login"));
		mainPanel.add(this.login);
		mainPanel.add(new JLabel("Password"));
		mainPanel.add(this.password);
		SpringUtilities.makeCompactGrid(mainPanel, 2, 2, FIELD_PADDING, FIELD_PADDING, FIELD_PADDING, FIELD_PADDING);
		this.add(mainPanel);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, FIELD_PADDING, FIELD_PADDING));
		buttons.add(new JButton(new OkAction()));
		buttons.add(new JButton(new ExitAction()));
		this.add(buttons, BorderLayout.SOUTH);

		this.setTitle("Login — UniTable Client");

		this.pack();
		this.setMinimumSize(this.getPreferredSize());
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
	}

	private void onExit() {
		this.setStatus(Status.EXIT);
		this.setVisible(false);
		this.dispose();
	}

	private void onOk() {
		try {
			this.props.put("user", this.login.getText());
			this.props.put("password", String.valueOf(this.password.getPassword()));
			this.connection = DriverManager.getConnection(this.url, props);
		} catch (SQLException e) {
			// TODO: Add details to error message
			System.err.println(e.getLocalizedMessage());
			JOptionPane.showMessageDialog(this, "Login to database failed. Please, check entered login and password and availability in database server.", "Login failed", JOptionPane.ERROR_MESSAGE);
			return;
		}

		this.setStatus(Status.CONNECTED);
		this.setVisible(false);
		this.dispose();
	}

	public Status getStatus() {
		return status;
	}

	protected void setStatus(Status status) {
		this.status = status;
	}

	public Connection getConnection() {
		return connection;
	}

	protected void setConnection(Connection connection) {
		this.connection = connection;
	}

	public enum Status {
		CONNECTED, EXIT
	}

	private class OkAction extends AbstractAction {
		private OkAction() {
			this.putValue(ACCELERATOR_KEY, KeyEvent.VK_ENTER);
			this.putValue(OkAction.NAME, "Connect");
			this.putValue(OkAction.SHORT_DESCRIPTION, "Connect to database");
		}

		public void actionPerformed(ActionEvent e) {
			onOk();
		}
	}

	private class ExitAction extends AbstractAction {
		private ExitAction() {
			this.putValue(ACCELERATOR_KEY, KeyEvent.VK_ESCAPE);
			this.putValue(OkAction.NAME, "Exit");
			this.putValue(OkAction.SHORT_DESCRIPTION, "Leave program");
		}

		public void actionPerformed(ActionEvent e) {
			onExit();
		}
	}
}
