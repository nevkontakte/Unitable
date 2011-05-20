package com.nevkontakte.unitable.client.reports;

import com.nevkontakte.unitable.client.RowSetComboBoxModel;
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
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 19.05.11
 * Time: 22:30
 */
public class StudentsReport extends BasicReport{

	private JTextField group;
	private UnitableFkSelector grade;
	private JSpinner childGr;
	private JSpinner childLo;
	private JSpinner stipendGr;
	private JSpinner stipendLo;
	private JSpinner yearGr;
	private JSpinner yearLo;
	private JComboBox gender;

	public StudentsReport(Window parent, Connection db) throws SQLException {
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

		// Stipend filtering
		JLabel stipendGrL = new JLabel("Stipend ≥");
		stipendGr = new JSpinner(new SpinnerNumberModel(0, 0, 50000, 100));
		stipendGr.setEnabled(false);
		final JCheckBox stipendGrC = new JCheckBox();
		stipendGrC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				stipendGr.setEnabled(stipendGrC.isSelected());
			}
		});
		this.addToForm(stipendGrL);
		this.addToForm(stipendGr);
		this.addToForm(stipendGrC);

		// Stipend filtering
		JLabel stipendLoL = new JLabel("Stipend ≤");
		stipendLo = new JSpinner(new SpinnerNumberModel(0, 0, 50000, 100));
		stipendLo.setEnabled(false);
		final JCheckBox stipendLoC = new JCheckBox();
		stipendLoC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				stipendLo.setEnabled(stipendLoC.isSelected());
			}
		});
		this.addToForm(stipendLoL);
		this.addToForm(stipendLo);
		this.addToForm(stipendLoC);

		// Gender filtering
		UnitableRowSet genders = new UnitableRowSet(db);
		genders.setCommand("SELECT DISTINCT people_gender FROM people");
		JLabel genderL = new JLabel("Gender:");
		gender = new JComboBox(new RowSetComboBoxModel(genders));
		gender.setEnabled(false);
		final JCheckBox genderC = new JCheckBox();
		genderC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				gender.setEnabled(genderC.isSelected());
			}
		});
		this.addToForm(genderL);
		this.addToForm(gender);
		this.addToForm(genderC);

		// Children filtering
		JLabel childGrL = new JLabel("Children ≥");
		childGr = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		childGr.setEnabled(false);
		final JCheckBox childGrC = new JCheckBox();
		childGrC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				childGr.setEnabled(childGrC.isSelected());
			}
		});
		this.addToForm(childGrL);
		this.addToForm(childGr);
		this.addToForm(childGrC);

		// Children filtering
		JLabel childLoL = new JLabel("Children ≤");
		childLo = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		childLo.setEnabled(false);
		final JCheckBox childLoC = new JCheckBox();
		childLoC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				childLo.setEnabled(childLoC.isSelected());
			}
		});
		this.addToForm(childLoL);
		this.addToForm(childLo);
		this.addToForm(childLoC);

		// Birth filtering
		JLabel yearGrL = new JLabel("Year ≥");
		yearGr = new JSpinner(new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR) -20, 0, Calendar.getInstance().get(Calendar.YEAR)+1, 1));
		yearGr.setEditor(new JSpinner.NumberEditor(yearGr, "#"));
		yearGr.setEnabled(false);
		final JCheckBox yearGrC = new JCheckBox();
		yearGrC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				yearGr.setEnabled(yearGrC.isSelected());
			}
		});
		this.addToForm(yearGrL);
		this.addToForm(yearGr);
		this.addToForm(yearGrC);

		// Birth filtering
		JLabel yearLoL = new JLabel("Year ≤");
		yearLo = new JSpinner(new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR) -10, 0, Calendar.getInstance().get(Calendar.YEAR) +1, 1));
		yearLo.setEditor(new JSpinner.NumberEditor(yearLo, "#"));
		yearLo.setEnabled(false);
		final JCheckBox yearLoC = new JCheckBox();
		yearLoC.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				yearLo.setEnabled(yearLoC.isSelected());
			}
		});
		this.addToForm(yearLoL);
		this.addToForm(yearLo);
		this.addToForm(yearLoC);

		this.layoutForm(9, 3);
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

		if(this.childGr.isEnabled()) {
			int childGr = (Integer)this.childGr.getValue();
			where.add(String.format("people_children_number >= %d", childGr));
		}

		if(this.childLo.isEnabled()) {
			int childLo = (Integer)this.childLo.getValue();
			where.add(String.format("people_children_number <= %d", childLo));
		}

		if(this.stipendGr.isEnabled()) {
			int stipendGr = (Integer) this.stipendGr.getValue();
			where.add(String.format("student_stipend >= %d", stipendGr));
		}

		if(this.stipendLo.isEnabled()) {
			int stipendLo = (Integer) this.stipendLo.getValue();
			where.add(String.format("student_stipend <= %d", stipendLo));
		}

		if(this.yearGr.isEnabled()) {
			int yearGr = (Integer) this.yearGr.getValue();
			where.add(String.format("people_birth >= TO_DATE('%d', 'YYYY')", yearGr));
		}

		if(this.yearLo.isEnabled()) {
			int yearLo = (Integer) this.yearLo.getValue();
			where.add(String.format("people_birth < TO_DATE('%d', 'YYYY')", yearLo+1));
		}

		if(this.gender.isEnabled()) {
			String gender = this.gender.getSelectedItem().toString().replace("'", "\\'");
			where.add(String.format("people_gender = '%s'", gender));
		}

		if(where.size() > 0) {
			sql = String.format("%s WHERE %s", sql, this.joinStrings(where, " AND "));
		}
		
		query.setCommand(sql);
		return query;
	}

	private String joinStrings(String[] strings, String delimiter) {
		StringBuilder joined = new StringBuilder();
		for(int i = 0; i < strings.length; i++) {
			if(i > 0) {
				joined.append(delimiter);
			}
			joined.append(strings[i]);
		}
		return joined.toString();
	}

	private String joinStrings(ArrayList<String> strings, String delimiter) {
		String[] s = new String[strings.size()];
		strings.toArray(s);
		return this.joinStrings(s, delimiter);
	}
}
