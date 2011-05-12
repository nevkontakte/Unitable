package com.nevkontakte.unitable.client;

import com.nevkontakte.unitable.model.DatabaseModel;
import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.TableModel;
import com.nevkontakte.unitable.view.UnitableView;
import com.nevkontakte.unitable.view.UnitableViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 11.05.11
 * Time: 8:38
 */
public class MainFrame extends JFrame{
	protected final Connection db;
	protected final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	public MainFrame(Connection db) throws HeadlessException {
		this.db = db;

		this.setPreferredSize(new Dimension(900, 500));
		this.setSize(this.getPreferredSize());
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("Unitable Client");

		this.add(this.tabs);

		// Initialize menu
		JMenuBar menu = new JMenuBar();

		// File
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(new ExitAction()));
		menu.add(fileMenu);

		// Tables
		JMenu tablesMenu = new JMenu("Tables");
		try {
			DatabaseModel dbModel = new DatabaseModel(this.db);
			for(String table : dbModel.getTables()) {
				TableModel tableModel = dbModel.getTable(table);
				tablesMenu.add(new JMenuItem(new TableEditAction(tableModel)));
			}
		} catch (SQLException e) {
			// TODO: Handle exception
			e.printStackTrace();
		}
		menu.add(tablesMenu);

		this.setJMenuBar(menu);
	}

	protected void onExit() {
		this.setVisible(true);
		this.dispose();
	}

	protected void onTableEdit(TableModel model) {
		try {
			UnitableViewModel viewModel = new UnitableViewModel(new TableData(model));
			this.tabs.addTab(model.getTableHumanName(), new UnitableView(viewModel));
			this.tabs.setTabComponentAt(this.tabs.getTabCount()-1, new CloseTabComponent(model.getTableHumanName()));
			this.tabs.setSelectedIndex(this.tabs.getTabCount()-1);
		} catch (SQLException e) {
			// TODO: Handle exception
			e.printStackTrace();
		}
	}

	private class TableEditAction extends AbstractAction {
		private final TableModel table;

		private TableEditAction(TableModel model) {
			this.table = model;
			this.putValue(TableEditAction.NAME, model.getTableHumanName());
			this.putValue(TableEditAction.SHORT_DESCRIPTION, "Edit table contents");
		}

		public void actionPerformed(ActionEvent e) {
			onTableEdit(table);
		}
	}

	private class ExitAction extends AbstractAction {

		private ExitAction() {
			this.putValue(TableEditAction.NAME, "Exit");
			this.putValue(TableEditAction.SHORT_DESCRIPTION, "Exit from Unitable application");
		}

		public void actionPerformed(ActionEvent e) {
			onExit();
		}
	}

	private class CloseTabComponent extends JPanel implements ActionListener {
		private String title;
		private CloseTabComponent(String title) {
			this.title = title;
			this.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
			JLabel label = new JLabel(title);

			this.add(label);
			JButton close = new JButton() {
				{
					this.setText("×");
//					int size = this.getFontMetrics(this.getFont()).get;
					this.setPreferredSize(new Dimension(this.getPreferredSize().width, this.getPreferredSize().width));
					this.setFocusable(false);
					this.setBorderPainted(false);
				}
			};
			close.addActionListener(this);
			this.add(close);

			this.setOpaque(false);
		}

		@Override
		public String getName() {
			return this.title;
		}

		public void actionPerformed(ActionEvent e) {
			int index = tabs.indexOfTabComponent(this);
			if(index != -1) {
				tabs.remove(index);
			}
		}
	}
}
