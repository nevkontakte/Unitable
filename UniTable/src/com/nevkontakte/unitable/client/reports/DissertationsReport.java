package com.nevkontakte.unitable.client.reports;

import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.TableModel;
import com.nevkontakte.unitable.model.UnitableRowSet;
import com.nevkontakte.unitable.view.UnitableFkSelector;

import javax.sql.rowset.JdbcRowSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 19.05.11
 * Time: 22:30
 */
public class DissertationsReport extends BasicReport{

	private UnitableFkSelector faculty;
	private UnitableFkSelector department;

	public DissertationsReport(Window parent, Connection db) throws SQLException {
		super(parent, db);
		this.hidden = false;

		// Faculty filtering
		JLabel facultyL = new JLabel("Faculty:");
		this.faculty = new UnitableFkSelector(new TableData(TableModel.get(db, "FACULTY")));
		faculty.setSelectedIndex(0);
		faculty.setEnabled(false);
		final JCheckBox facultyC = new JCheckBox();
		facultyC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				faculty.setEnabled(facultyC.isSelected());
			}
		});
		this.addToForm(facultyL);
		this.addToForm(faculty);
		this.addToForm(facultyC);

		// Faculty filtering
		JLabel departmentL = new JLabel("Department:");
		this.department = new UnitableFkSelector(new TableData(TableModel.get(db, "DEPARTMENT")));
		department.setSelectedIndex(0);
		department.setEnabled(false);
		final JCheckBox departmentC = new JCheckBox();
		departmentC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				department.setEnabled(departmentC.isSelected());
			}
		});
		this.addToForm(departmentL);
		this.addToForm(department);
		this.addToForm(departmentC);

		this.layoutForm(2, 3);
		this.pack();
	}

	@Override
	public String getReportName() {
		return "List teachers dissertations and diplomas";
	}

	@Override
	public UnitableRowSet buildQuery() throws SQLException {
		UnitableRowSet query = new UnitableRowSet(db);
		query.setType(JdbcRowSet.TYPE_SCROLL_INSENSITIVE);
		String sql =
				"SELECT people_fname, people_mname, people_lname, teacher_rank_topic, teacher_rank_type_title, department_title, faculty_title " +
						"FROM teacher_rank tr " +
						"LEFT JOIN teacher t ON t.teacher_id = tr.teacher_id " +
						"LEFT JOIN people p ON t.people_id = p.people_id " +
						"LEFT JOIN teacher_rank_type trt ON trt.teacher_rank_type_id = tr.teacher_rank_type_id " +
						"LEFT JOIN teacher_department td ON td.teacher_id = t.teacher_id " +
						"LEFT JOIN department d ON d.department_id = td.department_id " +
						"LEFT JOIN faculty f ON f.faculty_id = d.faculty_id ";

		ArrayList<String> where = new ArrayList<String>();
		
		if(this.faculty.isEnabled()) {
			Integer faculty = (Integer) this.faculty.getSelectedForeignKey();
			where.add(String.format("faculty_id = %d", faculty));
		}

		if(this.department.isEnabled()) {
			Integer department = (Integer) this.department.getSelectedForeignKey();
			where.add(String.format("department_id = %d", department));
		}

		if(where.size() > 0) {
			sql = String.format("%s WHERE %s", sql, this.joinStrings(where, " AND "));
		}
		
		query.setCommand(sql);
		return query;
	}
}
