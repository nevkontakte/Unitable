package com.nevkontakte.unitable.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * User: aleks
 * Date: 25.03.11
 * Time: 18:08
 */
public class TableModel {
	private final Connection db;
	protected final String tableName;

	protected final LinkedHashMap<String, ColumnModel> columns = new LinkedHashMap<String, ColumnModel>();
	protected final ArrayList<ColumnModel> primaryKeys = new ArrayList<ColumnModel>();
	protected final ArrayList<ForeignKeyModel> foreignKeys = new ArrayList<ForeignKeyModel>();

	public TableModel(Connection db, String tableName) throws SQLException {
		this.db = db;
		this.tableName = tableName;
		this.loadTableSchema();
	}

	private void loadTableSchema() throws SQLException {
		DatabaseMetaData meta = this.db.getMetaData();

		// Load columns
		ResultSet colDesc = meta.getColumns(this.db.getCatalog(), null, this.tableName, null);
		while (colDesc.next()) {
			ColumnModel col = new ColumnModel(colDesc);
			this.columns.put(col.getName(), col);
		}
		colDesc.close();

		// Load primary keys
		ResultSet pkDesc = meta.getPrimaryKeys(this.db.getCatalog(), null, this.tableName);
		while (pkDesc.next()) {
			this.primaryKeys.add(this.columns.get(pkDesc.getString("COLUMN_NAME")));
		}
		pkDesc.close();

		// Load foreign key model
		ResultSet fkDesc = meta.getImportedKeys(this.db.getCatalog(), null, this.tableName);
		while (fkDesc.next()) {
			this.foreignKeys.add(new ForeignKeyModel(fkDesc));
		}
		fkDesc.close();
	}

	public Connection getDb() {
		return db;
	}

	public String getTableName() {
		return tableName;
	}

	public HashMap<String, ColumnModel> getColumns() {
		return columns;
	}

	public ArrayList<ColumnModel> getPrimaryKeys() {
		return primaryKeys;
	}

	public ArrayList<ForeignKeyModel> getForeignKeys() {
		return foreignKeys;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("Table name: " + this.tableName + '\n');
		s.append("Columns:\n");
		for (ColumnModel c : this.columns.values()) {
			s.append('\t' + c.toString() + '\n');
		}
		s.append("Primary keys:");
		for (ColumnModel c : this.primaryKeys) {
			s.append(' ' + c.getName());
		}
		s.append('\n');
		s.append("Foreign keys:\n");
		for (ForeignKeyModel fk : this.foreignKeys) {
			s.append('\t' + fk.toString() + '\n');
		}

		return s.toString();
	}
}