package com.nevkontakte.unitable.client.reports;

import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.TableModel;
import com.nevkontakte.unitable.model.UnitableRowSet;
import com.nevkontakte.unitable.view.UnitableFkSelector;

import javax.sql.rowset.JdbcRowSet;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

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

		// Children filtering
		JLabel childL = new JLabel("Children:");
		final JSpinner child = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		child.setEnabled(false);
		final JCheckBox childC = new JCheckBox();
		childC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				child.setEnabled(childC.isSelected());
			}
		});
		this.addToForm(childL);
		this.addToForm(child);
		this.addToForm(childC);

		// Stipend filtering
		try {
			JLabel stipendL = new JLabel("Stipend:");
			final JFormattedTextField stipend;
			stipend = new JFormattedTextField(new MaskFormatter("#####-#####"));
			stipend.setToolTipText("Format: XXXXX-XXXXX");
			stipend.setColumns(15);
			stipend.setEnabled(false);
			final JCheckBox stipendC = new JCheckBox();
			stipendC.setAction(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					stipend.setEnabled(stipendC.isSelected());
				}
			});
			this.addToForm(stipendL);
			this.addToForm(stipend);
			this.addToForm(stipendC);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Birth filtering
		try {
			JLabel yearL = new JLabel("Year:");
			final JFormattedTextField year;
			year = new JFormattedTextField(new MaskFormatter("####-####"));
			year.setToolTipText("Format: YYYY-YYYY");
			year.setColumns(15);
			year.setEnabled(false);
			final JCheckBox yearC = new JCheckBox();
			yearC.setAction(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					year.setEnabled(yearC.isSelected());
				}
			});
			this.addToForm(yearL);
			this.addToForm(year);
			this.addToForm(yearC);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		this.layoutForm(5, 3);
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
				"SELECT people_fname, people_mname, people_lname, people_gender, people_birth, people_children_number, group_number, student_stipend " +
						"FROM student s " +
						"LEFT JOIN people p ON s.people_id = p.people_id " +
						"LEFT JOIN group_ g ON g.group_id = s.group_id";
		query.setCommand(sql);
		return query;
	}
}
