package com.nevkontakte.unitable.client;

import com.nevkontakte.unitable.client.reports.*;
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
	protected final JProgressBar idle = new JProgressBar(0, 100);

	public MainFrame(Connection db) throws HeadlessException {
		this.db = db;

		this.setPreferredSize(new Dimension(900, 500));
		this.setSize(this.getPreferredSize());
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Unitable Client");

		this.add(this.tabs);

		// Status bar
		JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.idle.setEnabled(false);
		status.add(this.idle);
		this.add(status, BorderLayout.SOUTH);

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
		//reportMenu.add(new JMenuItem(new ReportShowAction(new BasicReport(this, this.db))));
		try {
			reportMenu.add(new JMenuItem(new ReportShowAction(new StudentsReport(this, this.db))));
		} catch (SQLException e) {
			// Ignore buggy report
		}
		try {
			reportMenu.add(new JMenuItem(new ReportShowAction(new TeachersReport(this, this.db))));
		} catch (SQLException e) {
			// Ignore buggy report
		}
		try {
			reportMenu.add(new JMenuItem(new ReportShowAction(new DissertationsReport(this, this.db))));
		} catch (SQLException e) {
			// Ignore buggy report
		}
		try {
			reportMenu.add(new JMenuItem(new ReportShowAction(new TeachingDepartmentsReport(this, this.db))));
		} catch (SQLException e) {
			// Ignore buggy report
		}
		try {
			reportMenu.add(new JMenuItem(new ReportShowAction(new Examinators(this, this.db))));
		} catch (SQLException e) {
			// Ignore buggy report
		}
		reportMenu.add(new JMenuItem(new ReportShowAction(new StudentsByCreditMark(this, this.db))));
		reportMenu.add(new JMenuItem(new ReportShowAction(new StudentsBySession(this, this.db))));
		try {
			reportMenu.add(new JMenuItem(new ReportShowAction(new StudentsBySupervisor(this, this.db))));
		} catch (SQLException e) {
			// Ignore buggy report
		}
		reportMenu.add(new JMenuItem(new ReportShowAction(new StudentsByTeacherMark(this, this.db))));
		reportMenu.add(new JMenuItem(new ReportShowAction(new Supervisors(this, this.db))));
		reportMenu.add(new JMenuItem(new ReportShowAction(new TeacherByClassType(this, this.db))));
		reportMenu.add(new JMenuItem(new ReportShowAction(new TeacherBySubject(this, this.db))));
		reportMenu.add(new JMenuItem(new ReportShowAction(new TeacherLoad(this, this.db))));

		menu.add(reportMenu);

		this.setJMenuBar(menu);
	}

	protected void onExit() {
		this.setVisible(false);
		this.dispose();
		try {
			this.db.close();
		} catch (SQLException e) {
			System.err.println(e.getLocalizedMessage());
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}
		System.exit(0);
	}

	protected void onTableEdit(final TableModel model) {
		int index = this.tabs.indexOfTab(model.getTableHumanName());
		if(index == -1) {
			this.setIdle(true);
			new Thread(new Runnable() {
				public void run() {
					try {
						final UnitableViewModel viewModel = new UnitableViewModel(new TableData(model));
						final UnitableView component = new UnitableView(viewModel);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								tabs.addTab(model.getTableHumanName(), component);
								tabs.setTabComponentAt(tabs.getTabCount() - 1, new CloseTabComponent(model.getTableHumanName()));
								tabs.setSelectedIndex(tabs.getTabCount() - 1);
								setIdle(false);
							}
						});
					} catch (SQLException e) {
						// TODO: Handle exception
						e.printStackTrace();
					}
				}
			}).run();
		}
		else {
			this.tabs.setSelectedIndex(index);
		}
	}

	protected void onReportView(final ReportParametersDialog dialog) {
		dialog.setVisible(true);
		if(!dialog.isOk()) {
			return;
		}
		this.setIdle(true);
		new Thread(new Runnable() {
			public void run() {
				try {
					UnitableRowSet rowSet = dialog.buildQuery();
					if(rowSet == null) {
						return;
					}
					rowSet.executeOnce();
					final RowSetView component = new RowSetView(rowSet);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							tabs.addTab(dialog.getReportName(), component);
							tabs.setTabComponentAt(tabs.getTabCount() - 1, new CloseTabComponent(dialog.getReportName()));
							tabs.setSelectedIndex(tabs.getTabCount() - 1);
							setIdle(false);
						}
					});
				} catch (SQLException e) {
					// TODO: Error handling
					e.printStackTrace();
				}
			}
		}).run();
	}

	protected void setIdle(boolean status) {
		this.idle.setEnabled(status);
		this.idle.setIndeterminate(status);
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
