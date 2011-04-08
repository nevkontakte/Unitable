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

	public void executeOnce() throws SQLException {
		if (!this.isExecuted) {
			this.execute();
		}
	}

	public boolean isExecuted() {
		return isExecuted;
	}
}
