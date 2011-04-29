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
	private UnitableRowSet tableJoinedContents;

	public TableData(TableModel tableModel) throws SQLException {
		this.tableModel = tableModel;

		Connection db = this.tableModel.getDb();
		this.tableContents = new UnitableRowSet(db);
		this.tableContents.setConcurrency(UnitableRowSet.CONCUR_UPDATABLE);
		this.tableContents.setReadOnly(false);
		this.tableContents.setCommand(this.buildSelectCommand());
		this.tableJoinedContents = new UnitableRowSet(db);
		this.tableJoinedContents.setCommand(this.buildJoinedSelectCommand());
	}

	public UnitableRowSet getTableContents(boolean joinFk) throws SQLException {
		if(joinFk) {
			this.tableJoinedContents.executeOnce();
			return this.tableJoinedContents;
		} else {
			this.tableContents.executeOnce();
			return this.tableContents;
		}
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

	private String buildJoinedSelectCommand() throws SQLException {
		StringBuffer columnString = new StringBuffer();
		StringBuffer joinString = new StringBuffer();

		Set<String> columns = this.tableModel.getColumns().keySet();
		int fragmentCount = 0;
		for (String column : columns) {
			if (fragmentCount > 0) {
				columnString.append(", ");
			}
			columnString.append(this.quoteIdentifier(this.tableModel.getTableName()) + '.' + this.quoteIdentifier(column));
			fragmentCount++;
		}
		
		for(ForeignKeyModel fk : this.tableModel.getForeignKeys()) {
			TableModel foreignTable = TableModel.get(this.tableModel.getDb(), fk.getPkTableName());
			for(ColumnModel column : foreignTable.getColumns().values()) {
				if(column.isHumanFk()) {
					if (fragmentCount > 0) {
						columnString.append(", ");
					}

					columnString.append(this.quoteIdentifier(fk.getPkTableName()) + '.' + this.quoteIdentifier(column.getName()));
					
					fragmentCount++;
				}
			}

			joinString.append(" LEFT JOIN "+this.quoteIdentifier(fk.getPkTableName())+
					" ON " + this.quoteIdentifier(fk.getFkTableName()) + '.' + this.quoteIdentifier(fk.getFkColumnName()) +
					" = " + this.quoteIdentifier(fk.getPkTableName()) + '.' + this.quoteIdentifier(fk.getPkColumnName()));
		}

		// TODO: Add ORDER BY primary key
		return String.format("SELECT %s FROM (%s) %s;", columnString.toString(), this.quoteIdentifier(this.tableModel.getTableName()), joinString.toString());
	}

	private String quoteIdentifier(String identifier) throws SQLException {
		String quoteString = this.tableModel.getDb().getMetaData().getIdentifierQuoteString();
		return quoteString + identifier + quoteString;
	}

	public String getFkHumanValue(String fkColumnName, int row) {
		try {
			UnitableRowSet data = this.getTableContents(true);
			data.absolute(row + 1);
			StringBuffer value = new StringBuffer();
			int fragments = 0;
			ForeignKeyModel fkModel = this.tableModel.getForeignKey(this.tableModel.getColumns().get(fkColumnName));
			for(String column : fkModel.getFkCols()) {
				if(fragments > 0) {
					value.append(", ");
				}
				value.append(data.getString(column));
				fragments ++ ;
			}
			return value.toString();
		} catch (SQLException e) {
			// TODO: handle nicely
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		}
	}
}
