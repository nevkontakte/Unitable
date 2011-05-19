package com.nevkontakte.unitable.client.reports;

import com.nevkontakte.unitable.foreign.SpringUtilities;
import com.nevkontakte.unitable.model.UnitableRowSet;

import javax.sql.rowset.JdbcRowSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 13.05.11
 * Time: 17:59
 */
public class BasicReport extends JDialog implements ReportParametersDialog{
	private boolean isOk = false;
	protected Connection db;
	protected boolean hidden = true;
	protected final static int PAD = 5;
	private JPanel controls = new JPanel(new SpringLayout());

	public BasicReport(Window parent, Connection db) {
		super(parent);
		this.setLayout(new BorderLayout());
		this.setModal(true);
		this.db = db;
		this.isOk = true;
		this.add(this.controls, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton ok = new JButton(new AbstractAction() {
			{
				this.putValue(AbstractAction.NAME, "OK");
			}
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		buttons.add(ok);
		JButton cancel = new JButton(new AbstractAction() {
			{
				this.putValue(AbstractAction.NAME, "Cancel");
			}
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttons.add(cancel);
		this.add(buttons, BorderLayout.SOUTH);
	}

	protected void onOk() {
		this.isOk = true;
		this.setVisible(false);
	}

	protected void onCancel() {
		this.isOk = false;
		this.setVisible(false);
	}

	public boolean isOk() {
		return isOk;
	}

	public UnitableRowSet buildQuery() throws SQLException {
		UnitableRowSet query = new UnitableRowSet(db);
		query.setType(JdbcRowSet.TYPE_SCROLL_INSENSITIVE);
		query.setCommand("SELECT * FROM dual");
		return query;
	}

	public String getReportName() {
		return "Test report";
	}

	@Override
	public void setVisible(boolean b) {
		if(b == true && this.hidden) {
			b = false;
		}
		this.setLocationRelativeTo(this.getParent());
		super.setVisible(b);
	}

	protected void addToForm(Component c) {
		this.controls.add(c);
	}

	protected void layoutForm(int rows, int cols) {
		SpringUtilities.makeCompactGrid(this.controls, rows, cols, PAD, PAD, PAD, PAD);
	}
}
