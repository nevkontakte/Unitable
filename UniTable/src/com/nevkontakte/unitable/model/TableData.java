package com.nevkontakte.unitable.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * User: aleks
 * Date: 31.03.11
 * Time: 16:04
 */
public class TableData {
	protected final TableModel tableModel;
	private UnitableRowSet tableContents;

	public TableData(TableModel tableModel) throws SQLException {
		this.tableModel = tableModel;

		Connection db = this.tableModel.getDb();
		this.tableContents = new UnitableRowSet(db);
		this.tableContents.setConcurrency(UnitableRowSet.CONCUR_UPDATABLE);
		this.tableContents.setReadOnly(false);
		this.tableContents.setCommand(this.buildSelectCommand());
	}

	public UnitableRowSet getTableContents(boolean joinFk) throws SQLException {
		this.tableContents.executeOnce();
		return this.tableContents;
	}

	public TableModel getTableModel() {
		return tableModel;
	}

	public int getRowCount() throws SQLException {
		this.tableContents.executeOnce();
		int backupRow = this.tableContents.getRow();

		int rowCount = this.tableContents.last() ? this.tableContents.getRow() : 0;

		// Restore position
		if (backupRow == 0) {
			this.tableContents.beforeFirst();
		} else {
			this.tableContents.absolute(backupRow);
		}
		return rowCount;
	}

	public void insertRow(Map<String, Object> values) throws SQLException {
		this.tableContents.moveToInsertRow();
		for(String column : values.keySet()) {
			this.tableContents.updateObject(column, values.get(column));
		}
		this.tableContents.insertRow();
	}

	private String buildSelectCommand() throws SQLException {
		StringBuffer columnString = new StringBuffer();
		Set<String> columns = this.tableModel.getColumns().keySet();
		int fragmentCount = 0;
		for (String column : columns) {
			if (fragmentCount > 0) {
				columnString.append(", ");
			}
			columnString.append(this.quoteIdentifier(column));
			fragmentCount++;
		}
		// TODO: Add ORDER BY primary key
		return String.format("SELECT %s FROM %s;", columnString.toString(), this.quoteIdentifier(this.tableModel.getTableName()));
	}

	private String quoteIdentifier(String identifier) throws SQLException {
		String quoteString = this.tableModel.getDb().getMetaData().getIdentifierQuoteString();
		return quoteString + identifier + quoteString;
	}
}
