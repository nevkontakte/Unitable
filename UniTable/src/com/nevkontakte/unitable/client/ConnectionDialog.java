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
	private JTextField urlField;

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

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel loginPanel = new JPanel(new SpringLayout());
		loginPanel.add(new JLabel("Login"));
		loginPanel.add(this.login);
		loginPanel.add(new JLabel("Password"));
		loginPanel.add(this.password);
		SpringUtilities.makeCompactGrid(loginPanel, 2, 2, FIELD_PADDING, FIELD_PADDING, FIELD_PADDING, FIELD_PADDING);
		mainPanel.add(loginPanel);

		JPanel advancedPanel = new JPanel(new BorderLayout());

		final JPanel advancedOptions = new JPanel(new SpringLayout());
		advancedOptions.setVisible(false);
		advancedOptions.add(new JLabel("Connection URL:"));
		urlField = new JTextField(this.url);
		urlField.setColumns(15);
		advancedOptions.add(urlField);
		SpringUtilities.makeCompactGrid(advancedOptions, 1, 2, FIELD_PADDING, FIELD_PADDING, FIELD_PADDING, FIELD_PADDING);
		advancedPanel.add(advancedOptions, BorderLayout.CENTER);

		JButton toggleAdvanced = new JButton();
		toggleAdvanced.setAction(new AbstractAction() {
			private Icon collapsed = new ImageIcon(getClass().getResource("resources/collapsed.png"));
			private Icon expanded = new ImageIcon(getClass().getResource("resources/expanded.png"));
			{
				this.putValue(AbstractAction.NAME, "Advanced");
				this.putValue(AbstractAction.SMALL_ICON, collapsed);
			}
			public void actionPerformed(ActionEvent e) {
				if(advancedOptions.isVisible()) {
					advancedOptions.setVisible(false);
					this.putValue(AbstractAction.SMALL_ICON, collapsed);
				} else {
					advancedOptions.setVisible(true);
					this.putValue(AbstractAction.SMALL_ICON, expanded);
				}
				mainPanel.revalidate();
				mainPanel.repaint();
				setSize(getWidth(), getPreferredSize().height);
			}
		});
		toggleAdvanced.setHorizontalAlignment(SwingConstants.LEFT);
		toggleAdvanced.setFocusPainted(false);
		toggleAdvanced.setBorderPainted(false);
		advancedPanel.add(toggleAdvanced, BorderLayout.NORTH);

		mainPanel.add(advancedPanel);

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
			this.connection = DriverManager.getConnection(this.urlField.getText(), props);
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
