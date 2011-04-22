package com.nevkontakte.unitable.model;

import com.sun.rowset.JdbcRowSetImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 08.04.11
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class UnitableRowSet extends JdbcRowSetImpl {
	private boolean isExecuted = false;

	public UnitableRowSet() {
		super();
	}

	public UnitableRowSet(Connection connection) throws SQLException {
		super(connection);
	}

	public UnitableRowSet(String s, String s1, String s2) throws SQLException {
		super(s, s1, s2);
	}

	public UnitableRowSet(ResultSet resultSet) throws SQLException {
		super(resultSet);
	}

	@Override
	public void execute() throws SQLException {
		this.isExecuted = true;
		super.execute();
	}

	@Override
	public boolean absolute(int i) throws SQLException {
		if(i == 0) {
			this.beforeFirst();
			return true;
		} else if(i == this.getRowCountUnsafe()+1) {
			this.moveToInsertRow();
			return true;
		} else {
			return super.absolute(i);
		}
	}

	public int getRowCount() throws SQLException {
		int backupRow = this.getRow();
		int rowCount = this.getRowCountUnsafe();
		this.absolute(backupRow);
		return rowCount;
	}

	protected int getRowCountUnsafe() throws SQLException {
		this.executeOnce();
		return this.last() ? this.getRow() : 0;
	}

	public void executeOnce() throws SQLException {
		if (!this.isExecuted) {
			this.execute();
		}
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	public void scheduleReExecution() {
		this.isExecuted = false;
	}
}
