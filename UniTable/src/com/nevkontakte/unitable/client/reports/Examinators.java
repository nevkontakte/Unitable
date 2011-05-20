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
public class Examinators extends BasicReport{
	private JTextField group;
	private UnitableFkSelector subject;

	public Examinators(Window parent, Connection db) throws SQLException {
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

		// Subject filtering
		JLabel subjectL = new JLabel("Subject:");
		this.subject = new UnitableFkSelector(new TableData(TableModel.get(db, "SUBJECT")));
		this.subject.setEnabled(false);
		this.subject.setSelectedIndex(0);
		final JCheckBox subjectC = new JCheckBox();
		subjectC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				subject.setEnabled(subjectC.isSelected());
			}
		});
		this.addToForm(subjectL);
		this.addToForm(subject);
		this.addToForm(subjectC);

		this.layoutForm(2, 3);
		this.pack();
	}

	@Override
	public UnitableRowSet buildQuery() throws SQLException {
		UnitableRowSet query = new UnitableRowSet(db);
		query.setType(JdbcRowSet.TYPE_SCROLL_INSENSITIVE);
		String sql =
				"SELECT DISTINCT people_fname, people_mname, people_lname, subject_title, group_number " +
						"FROM teacher t " +
						"LEFT JOIN people p ON t.people_id = p.people_id " +
						"INNER JOIN student_credit sc ON sc.teacher_id = t.teacher_id " +
						"LEFT JOIN plan pl ON pl.plan_id = sc.plan_id " +
						"LEFT JOIN subject su ON su.subject_id = pl.subject_id " +
						"LEFT JOIN student st ON st.student_id = sc.student_id " +
						"LEFT JOIN group_ gr ON gr.group_id = st.group_id ";

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

		if(this.subject.isEnabled()) {
			Integer subject = (Integer) this.subject.getSelectedForeignKey();
			where.add(String.format("subject_id = %d", subject));
		}

		if(where.size() > 0) {
			sql = String.format("%s WHERE %s", sql, this.joinStrings(where, " AND "));
		}

		query.setCommand(sql);
		return query;
	}

	@Override
	public String getReportName() {
		return "Examinators by subject, groups and semesters";
	}
}
