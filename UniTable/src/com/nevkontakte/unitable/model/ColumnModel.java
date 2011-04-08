package com.nevkontakte.unitable.model;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: aleks
 * Date: 25.03.11
 * Time: 18:40
 */
public class ColumnModel {
	private final String name;
	private final int type;
	private final int size;
	private final int decimalDigits;
	private final boolean nullable;
	private final String defaultValue;

	public ColumnModel(ResultSet metaRow) throws SQLException {
		this.name = metaRow.getString("COLUMN_NAME");
		this.type = metaRow.getInt("DATA_TYPE");
		this.size = metaRow.getInt("COLUMN_SIZE");
		this.decimalDigits = metaRow.getInt("DECIMAL_DIGITS");
		this.nullable = (metaRow.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
		this.defaultValue = metaRow.getString("COLUMN_DEF");
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(String.format("%s %s(%d", this.name, JdbcTypeHelper.getStringByInt(this.type), size));
		if (this.decimalDigits > 0) {
			buf.append(this.decimalDigits);
		}
		buf.append(')');
		if (!this.nullable) {
			buf.append(" NOT NULL");
		}
		if (this.defaultValue != null) {
			buf.append(" DEFAULT '" + this.defaultValue + "'");
		}
		return buf.toString();
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public int getSize() {
		return size;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public boolean isNullable() {
		return nullable;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
}
