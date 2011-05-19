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

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 19.05.11
 * Time: 22:30
 */
public class StudentsReport extends BasicReport{
	public StudentsReport(Window parent, Connection db) throws SQLException {
		super(parent, db);
		this.hidden = false;

		// Group filtering
		JLabel groupL = new JLabel("Group:");
		final JTextField group = new JTextField();
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
		final UnitableFkSelector grade = new UnitableFkSelector(new TableData(TableModel.get(db, "GRADE")));
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

		this.layoutForm(2, 3);
		this.pack();
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
