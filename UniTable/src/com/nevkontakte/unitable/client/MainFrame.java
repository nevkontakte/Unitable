package com.nevkontakte.unitable.client;

import com.nevkontakte.unitable.client.reports.ReportParametersDialog;
import com.nevkontakte.unitable.client.reports.TestReport;
import com.nevkontakte.unitable.model.DatabaseModel;
import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.TableModel;
import com.nevkontakte.unitable.model.UnitableRowSet;
import com.nevkontakte.unitable.view.UnitableView;
import com.nevkontakte.unitable.view.UnitableViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

		// Reports
		JMenu reportMenu = new JMenu("Reports");
		reportMenu.add(new JMenuItem(new ReportShowAction(new TestReport(this.db))));

		menu.add(reportMenu);

		this.setJMenuBar(menu);
	}

	protected void onExit() {
		this.setVisible(true);
		this.dispose();
	}

	protected void onTableEdit(TableModel model) {
		int index = this.tabs.indexOfTab(model.getTableHumanName());
		if(index == -1) {
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
		else {
			this.tabs.setSelectedIndex(index);
		}
	}

	protected void onReportView(ReportParametersDialog dialog) {
		dialog.setVisible(true);
		if(!dialog.isOk()) {
			return;
		}
		try {
			UnitableRowSet rowSet = dialog.buildQuery();
			this.tabs.addTab(dialog.getReportName(), new RowSetView(rowSet));
			this.tabs.setTabComponentAt(this.tabs.getTabCount()-1, new CloseTabComponent(dialog.getReportName()));
			this.tabs.setSelectedIndex(this.tabs.getTabCount()-1);
		} catch (SQLException e) {
			// TODO: Error handling
			e.printStackTrace();
		}
	}

	private class ReportShowAction extends AbstractAction {
		private ReportParametersDialog dialog;
		private ReportShowAction(ReportParametersDialog dialog) {
			this.dialog = dialog;
			this.putValue(TableEditAction.NAME, dialog.getReportName());
			this.putValue(TableEditAction.SHORT_DESCRIPTION, "Show report dialog");
		}

		public void actionPerformed(ActionEvent e) {
			onReportView(dialog);
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
		private int size;

		private CloseTabComponent(String title) {
			this.title = title;
			this.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
			JLabel label = new JLabel(title);

			this.add(label);
			JButton close = new JButton() {
				private BasicStroke stroke;
				{
					this.stroke = new BasicStroke(1.5f);
					size = this.getFontMetrics(this.getFont()).getAscent();
					this.setPreferredSize(new Dimension(size, size));
					this.setFocusable(false);
					this.setBorderPainted(false);
					this.setContentAreaFilled(false);
					this.setRolloverEnabled(true);
					this.setBorder(BorderFactory.createEtchedBorder());
					this.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseEntered(MouseEvent e) {
							setBorderPainted(true);
						}

						@Override
						public void mouseExited(MouseEvent e) {
							setBorderPainted(false);
						}
					});
				}

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setStroke(this.stroke);
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					float offset = size/4.0f;
					g2d.drawLine(Math.round(offset), Math.round(offset), Math.round(size-offset-1), Math.round(size-offset-1));
					g2d.drawLine(Math.round(offset), Math.round(size-offset-1), Math.round(size-offset-1), Math.round(offset));
					g2d.dispose();
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
