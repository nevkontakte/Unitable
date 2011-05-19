package com.nevkontakte.unitable.client.reports;

import com.nevkontakte.unitable.model.UnitableRowSet;

import javax.sql.rowset.JdbcRowSet;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 19.05.11
 * Time: 22:30
 */
public class StudentsReport extends BasicReport{
	public StudentsReport(Connection db) {
		super(db);
		this.hidden = true;
	}

	@Override
	public String getReportName() {
		return "List students";
	}

	@Override
	public UnitableRowSet buildQuery() throws SQLException {
		UnitableRowSet query = new UnitableRowSet(db);
		query.setType(JdbcRowSet.TYPE_SCROLL_INSENSITIVE);
		String sql =
				"SELECT people_fname, people_mname, people_lname, people_gender, people_birth, people_children_number, group_number " +
						"FROM student s " +
						"LEFT JOIN people p ON s.people_id = p.people_id " +
						"LEFT JOIN group_ g ON g.group_id = s.group_id";
		query.setCommand(sql);
		return query;
	}
}
