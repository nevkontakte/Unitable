package com.nevkontakte.unitable.model;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

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
	private final boolean hidden;
	private final String humanName;
	private final boolean humanFk;
	private final boolean marked;

	public ColumnModel(ResultSet metaRow) throws SQLException {
		this.name = metaRow.getString("COLUMN_NAME");
		this.type = metaRow.getInt("DATA_TYPE");
		this.size = metaRow.getInt("COLUMN_SIZE");
		this.decimalDigits = metaRow.getInt("DECIMAL_DIGITS");
		this.nullable = (metaRow.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
		this.defaultValue = metaRow.getString("COLUMN_DEF");

		// Parse magic remarks
		String remarks = metaRow.getString("REMARKS");

		// Set initial values for extended parameters
		boolean hidden = false;
		String humanName = null;
		boolean humanFk = false;
		boolean marked = false;
		
		while(remarks != null) { // Using while here as IF + GOTO replacement =)
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
			marked = true;

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
					humanName = value;
				}
				else if (tag.equals("HUMAN_FK")) {
					humanFk = true;
				}
			}
			break;
		}
		if(humanName == null) {
			humanName = name.replace('_', ' ').toLowerCase();
			humanName = humanName.substring(0, 1).toUpperCase()+humanName.substring(1);
		}
		this.hidden = hidden;
		this.humanName = humanName;
		this.humanFk = humanFk;
		this.marked = marked;
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

	public String exportComments() {
		StringBuffer buf = new StringBuffer();
		buf.append("UNITABLE;");
		if(this.hidden) {
			buf.append("HIDDEN;");
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

	public boolean isHidden() {
		return hidden;
	}

	public String getHumanName() {
		return humanName;
	}

	public boolean isHumanFk() {
		return humanFk;
	}

	public boolean isMarked() {
		return marked;
	}
}
