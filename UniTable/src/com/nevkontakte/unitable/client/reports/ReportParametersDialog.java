package com.nevkontakte.unitable.client.reports;

import com.nevkontakte.unitable.model.UnitableRowSet;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 13.05.11
 * Time: 17:56
 */
public interface ReportParametersDialog {
	public void setVisible(boolean visible);
	public boolean isOk();
	public UnitableRowSet buildQuery() throws SQLException;
	public String getReportName();
}
