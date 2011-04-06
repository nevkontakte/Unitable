package com.nevkontakte.unitable.model;

import com.sun.rowset.JdbcRowSetImpl;

import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: aleks
 * Date: 31.03.11
 * Time: 16:04
 */
public class TableData {
	protected final TableModel tableModel;
	private JdbcRowSet rowSet;

	public TableData(TableModel tableModel) throws SQLException {
		this.tableModel = tableModel;

		Connection db = this.tableModel.getDb();
		this.rowSet = new JdbcRowSetImpl(db);
		this.rowSet.setCommand(this.buildSelectCommand());
	}

	public RowSet getTableContents(boolean joinFk) {
		return this.rowSet;
	}

	private String buildSelectCommand() {
		return String.format("SELECT * FROM \"%s\";", this.tableModel.getTableName());
	}
}
