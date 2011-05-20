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
 * Date: 20.05.11
 * Time: 18:31
 */
public class StudentsBySupervisor extends BasicReport{
	private UnitableFkSelector supervisor;
	private UnitableFkSelector department;

	public StudentsBySupervisor(Window parent, Connection db) throws SQLException {
		super(parent, db);
		this.hidden = false;

		JLabel supervisorL = new JLabel("Supervisor:");
		this.supervisor = new UnitableFkSelector(new TableData(TableModel.get(db, "TEACHER")));
		this.supervisor.setEnabled(false);
		this.supervisor.setSelectedIndex(0);
		final JCheckBox supervisorC = new JCheckBox();
		supervisorC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				supervisor.setEnabled(supervisorC.isSelected());
			}
		});
		this.addToForm(supervisorL);
		this.addToForm(supervisor);
		this.addToForm(supervisorC);

		// Department filtering
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
	public UnitableRowSet buildQuery() throws SQLException {
		UnitableRowSet query = new UnitableRowSet(db);
		query.setType(JdbcRowSet.TYPE_SCROLL_INSENSITIVE);
		String sql =
				"SELECT people_fname, people_mname, people_lname, diploma_title " +
						"FROM diploma d " +
						"LEFT JOIN student s ON s.student_id = d.student_id " +
						"LEFT JOIN people p ON s.people_id = p.people_id " +
						"LEFT JOIN teacher t ON d.teacher_id = t.teacher_id ";

		ArrayList<String> where = new ArrayList<String>();

		if(this.supervisor.isEnabled()) {
			Integer supervisor = (Integer) this.supervisor.getSelectedForeignKey();
			where.add(String.format("teacher_id = %d", supervisor));
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

	@Override
	public String getReportName() {
		return "Students by supervisor";
	}
}
