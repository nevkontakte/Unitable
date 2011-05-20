package com.nevkontakte.unitable.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * User: aleks
 * Date: 25.03.11
 * Time: 18:08
 */
public class TableModel {
	private final Connection db;
	protected final String tableName;
	protected final String tableHumanName;
	private final boolean hidden;

	protected final LinkedHashMap<String, ColumnModel> columns = new LinkedHashMap<String, ColumnModel>();
	protected final ArrayList<ColumnModel> primaryKeys = new ArrayList<ColumnModel>();
	protected final ArrayList<ForeignKeyModel> foreignKeys = new ArrayList<ForeignKeyModel>();

	public TableModel(Connection db, String tableName) throws SQLException {
		this.db = db;
		this.tableName = tableName;

		// Parse table remarks
		boolean hidden = false;
		String humanTableName = null;

		while(true) { // Using while here as IF + GOTO replacement =)
			DatabaseMetaData meta = this.db.getMetaData();
			ResultSet tableMeta = meta.getTables(this.db.getCatalog(), null, this.tableName, new String[] {"TABLE"});
			if(!tableMeta.next()) {
				break;
			}
			String remarks = tableMeta.getString("REMARKS");
			if (remarks == null) {
				break;
			}
//			System.out.println("Remarks: "+remarks);
			// Initialize parser
			Scanner s = new Scanner(remarks);
			s.useDelimiter(";");

			// Check if parsing is possible and REMARKS are tagged with magic UNITABLE tag.
			if(!s.hasNext()) {
				break;
			}
			String magicTag = s.next();
			if(!magicTag.toUpperCase().equals("UNITABLE")) {
				break;
			}

			// Loop through tags
			while(s.hasNext()) {
				// Get next tag
				String tag = s.next(); // Tag name
				String value = null; // Tag value for tags which have it

				// Detect if tag has value
				if(tag.indexOf('=') != -1) {
					String[] parts = tag.split("=", 2);
					tag = parts[0];
					value = parts[1];
				}

				// Fix tag case
				tag = tag.toUpperCase();

				if (tag.equals("HIDDEN")) {
					hidden = true;
				}
				else if(tag.equals("HUMAN_NAME") && value != null) {
					humanTableName = value;
				}
			}
			tableMeta.close();
			break;
		}
		if(humanTableName == null) {
			humanTableName = tableName.replace('_', ' ').toLowerCase();
			humanTableName = humanTableName.substring(0, 1).toUpperCase()+humanTableName.substring(1);
		}
		this.tableHumanName = humanTableName;
		this.hidden = hidden;
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
			this.foreignKeys.add(new ForeignKeyModel(fkDesc, this.db));
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

	public ForeignKeyModel getForeignKey(ColumnModel column) {
		for(ForeignKeyModel fk : this.foreignKeys) {
			if(column.getName().equals(fk.getFkColumnName())) {
				return fk;
			}
		}

		return null;
	}

	public String getTableHumanName() {
		return tableHumanName;
	}

	public boolean isHidden() {
		return hidden;
	}

	private String exportComments() {
		StringBuffer buf = new StringBuffer();
		buf.append("UNITABLE;");
		if(this.hidden) {
			buf.append("HIDDEN;");
		}
		if(!this.tableHumanName.equals(this.tableName)) {
			buf.append("HUMAN_NAME=");
			buf.append(this.tableHumanName);
		}
		return buf.toString();
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("Table name: " + this.tableName + '\n');
		s.append("TableComments: " + this.exportComments()+'\n');
		s.append("Columns:\n");
		for (ColumnModel c : this.columns.values()) {
			s.append('\t' + c.toString() + '\n');
		}
		s.append("Column comments:\n");
		for (ColumnModel c : this.columns.values()) {
			s.append('\t' + c.getName() + ": " + c.exportComments() + '\n');
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

	private static HashMap<String, TableModel> models = new HashMap<String, TableModel>();
	public static TableModel get(Connection db, String tableName) throws SQLException {
		tableName = tableName.toUpperCase();
		if(models.containsKey(tableName)) {
			return models.get(tableName);
		}
		TableModel model = new TableModel(db, tableName);
		models.put(tableName, model);
		return model;
	}
}
