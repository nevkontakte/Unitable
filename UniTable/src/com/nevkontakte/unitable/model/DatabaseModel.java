package com.nevkontakte.unitable.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 11.05.11
 * Time: 14:27
 */
public class DatabaseModel {
	private final Connection db;
	private final LinkedList<String> tables = new LinkedList<String>();
	private final String catalog;

	public DatabaseModel(Connection db) throws SQLException {
		this.db = db;
		DatabaseMetaData meta = this.db.getMetaData();
		ResultSet tables = meta.getTables(this.db.getCatalog(), meta.getUserName(), null, null);
		while(tables.next()) {
			this.tables.add(tables.getString("TABLE_NAME"));
		}
		tables.close();
		this.catalog = this.db.getCatalog();
	}

	public List<String> getTables() {
		return this.tables;
	}

	public String getCatalog() {
		return catalog;
	}

	public Connection getDb() {
		return db;
	}

	public TableModel getTable(String table) throws SQLException {
		return TableModel.get(this.db, table);
	}

	@Override
	public String toString() {
		return String.format("Database name: %s\nTables (%d): %s\n", this.catalog, this.tables.size(), this.tables.toString());
	}
}
