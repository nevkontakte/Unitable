package com.nevkontakte.unitable.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * User: aleks
 * Date: 25.03.11
 * Time: 20:44
 */
public class ForeignKeyModel {
	private final String fkTableName;
	private final String fkColumnName;
	private final String pkTableName;
	private final String pkColumnName;

	protected final ArrayList<String> fkCols = new ArrayList<String>();

	public ForeignKeyModel(ResultSet rs, Connection db) throws SQLException {
		this.fkColumnName = rs.getString("FKCOLUMN_NAME");
		this.fkTableName = rs.getString("FKTABLE_NAME");
		this.pkColumnName = rs.getString("PKCOLUMN_NAME");
		this.pkTableName = rs.getString("PKTABLE_NAME");

		TableModel foreignTable = TableModel.get(db, this.getPkTableName());
		for(ColumnModel column : foreignTable.getColumns().values()) {
			if(column.isHumanFk()) {
				fkCols.add(column.getName());
			}
		}
	}

	public String toString() {
		return String.format("Foreign key: %s.%s, references %s.%s", this.fkColumnName, this.fkTableName, this.pkColumnName, this.pkTableName);
	}

	public String getFkTableName() {
		return fkTableName;
	}

	public String getFkColumnName() {
		return fkColumnName;
	}

	public String getPkTableName() {
		return pkTableName;
	}

	public String getPkColumnName() {
		return pkColumnName;
	}

	public ArrayList<String> getFkCols() {
		return fkCols;
	}
}
