package com.nevkontakte.unitable.client.reports;

import com.nevkontakte.unitable.model.UnitableRowSet;

import javax.sql.rowset.JdbcRowSet;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 13.05.11
 * Time: 17:59
 */
public class TestReport extends JDialog implements ReportParametersDialog{
	private boolean isOk = false;
	private Connection db;

	public TestReport(Connection db) {
		this.setModal(true);
		this.db = db;
		this.isOk = true;
	}

	public boolean isOk() {
		return isOk;
	}

	public UnitableRowSet buildQuery() throws SQLException {
		UnitableRowSet query = new UnitableRowSet(db);
		query.setType(JdbcRowSet.TYPE_SCROLL_INSENSITIVE);
		query.setCommand("SELECT * FROM faculty");
		query.execute();
		return query;
	}

	public String getReportName() {
		return "Test report";
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(false);
	}
}
