package com.nevkontakte.unitable.model;

import java.sql.ResultSet;
import java.sql.SQLException;

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

	public ForeignKeyModel(ResultSet rs) throws SQLException {
		this.fkColumnName = rs.getString("FKCOLUMN_NAME");
		this.fkTableName = rs.getString("FKTABLE_NAME");
		this.pkColumnName = rs.getString("PKCOLUMN_NAME");
		this.pkTableName = rs.getString("PKTABLE_NAME");
	}

	public String toString() {
		return String.format("Foreign key: %s.%s, references %s.%s", this.fkColumnName, this.fkTableName, this.pkColumnName, this.pkTableName);
	}
}
