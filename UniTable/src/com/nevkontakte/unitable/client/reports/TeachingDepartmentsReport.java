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
public class TeachingDepartmentsReport extends BasicReport{

	private JTextField group;
	private UnitableFkSelector grade;
	private JTextField semester;

	public TeachingDepartmentsReport(Window parent, Connection db) throws SQLException {
		super(parent, db);
		this.hidden = false;

		// Group filtering
		JLabel groupL = new JLabel("Group:");
		group = new JTextField();
		group.setColumns(15);
		group.setToolTipText("Group numbers separated by comma");
		group.setEnabled(false);
		final JCheckBox groupC = new JCheckBox();
		groupC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				group.setEnabled(groupC.isSelected());
			}
		});
		this.addToForm(groupL);
		this.addToForm(group);
		this.addToForm(groupC);

		// Grade filtering
		JLabel gradeL = new JLabel("Grade:");
		grade = new UnitableFkSelector(new TableData(TableModel.get(db, "GRADE")));
		grade.setEnabled(false);
		grade.setSelectedIndex(0);
		final JCheckBox gradeC = new JCheckBox();
		gradeC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				grade.setEnabled(gradeC.isSelected());
			}
		});
		this.addToForm(gradeL);
		this.addToForm(grade);
		this.addToForm(gradeC);

		// Group filtering
		JLabel semesterL = new JLabel("Semester:");
		semester = new JTextField();
		semester.setColumns(15);
		semester.setToolTipText("Semester numbers separated by comma");
		semester.setEnabled(false);
		final JCheckBox semesterC = new JCheckBox();
		semesterC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				semester.setEnabled(semesterC.isSelected());
			}
		});
		this.addToForm(semesterL);
		this.addToForm(semester);
		this.addToForm(semesterC);

		this.layoutForm(3, 3);
		this.pack();
	}

	@Override
	public String getReportName() {
		return "Teaching departments";
	}

	@Override
	public UnitableRowSet buildQuery() throws SQLException {
		UnitableRowSet query = new UnitableRowSet(db);
		query.setType(JdbcRowSet.TYPE_SCROLL_INSENSITIVE);
		String sql =
				"SELECT DISTINCT department_title, group_number, class_semester " +
						"FROM department d " +
						"INNER JOIN plan p ON p.department_id = d.department_id " +
						"LEFT JOIN class c ON c.plan_id = p.plan_id " +
						"LEFT JOIN grade g ON g.grade_id = p.grade_id " +
						"LEFT JOIN group_ gr ON gr.course_id = g.grade_id " +
						"LEFT JOIN faculty f ON f.faculty_id = g.faculty_id ";

		ArrayList<String> where = new ArrayList<String>();
		if(this.group.isEnabled()) {
			String[] groups = this.group.getText().split(",");
			for(int i = 0; i < groups.length; i++) {
				try {
					groups[i] = groups[i].trim();
					Integer.parseInt(groups[i]);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Invalid group list format", JOptionPane.ERROR_MESSAGE);
					return null;
				}
			}
			where.add(String.format("group_number IN (%s)", this.joinStrings(groups, ", ")));
		}

		if(this.grade.isEnabled()) {
			Object grade = this.grade.getSelectedForeignKey();
			if(grade != null) {
				where.add(String.format("COURSE_ID = %s", grade));
			}
		}

		if(this.semester.isEnabled()) {
			String[] semesters = this.semester.getText().split(",");
			for(int i = 0; i < semesters.length; i++) {
				try {
					semesters[i] = semesters[i].trim();
					Integer.parseInt(semesters[i]);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Invalid semester list format", JOptionPane.ERROR_MESSAGE);
					return null;
				}
			}
			where.add(String.format("class_semester IN (%s)", this.joinStrings(semesters, ", ")));
		}

		if(where.size() > 0) {
			sql = String.format("%s WHERE %s", sql, this.joinStrings(where, " AND "));
		}
		
		query.setCommand(sql);
		return query;
	}
}
