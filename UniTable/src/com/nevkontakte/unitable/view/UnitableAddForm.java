package com.nevkontakte.unitable.view;

import com.nevkontakte.unitable.model.ColumnModel;
import com.nevkontakte.unitable.model.ForeignKeyModel;
import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.TableModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 08.04.11
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
public class UnitableAddForm extends JPanel{
	protected final static int GAP = 5;
	protected final UnitableViewModel model;
	LinkedHashMap<String, JComponent> fields = new LinkedHashMap<String, JComponent>();

	public UnitableAddForm(final UnitableViewModel model) {
		this.model = model;
		this.setLayout(new BorderLayout());
		SpringLayout layout = new SpringLayout();
		JPanel inputs = new JPanel(layout);

		Border tmpBorder = null;
		final JButton add = new JButton("Add");
		TableModel tableModel = this.model.getTableData().getTableModel();
		for(ColumnModel columnModel : tableModel.getColumns().values()) {
			if(columnModel.isHidden()) {
				continue;
			}
			inputs.add(new JLabel(columnModel.getHumanName()+':'));
			JComponent field;
			ForeignKeyModel fkModel = tableModel.getForeignKey(columnModel);
			if(fkModel != null) {
				try {
					UnitableFkSelector fkField = new UnitableFkSelector(new TableData(TableModel.get(tableModel.getDb(), fkModel.getPkTableName())));
					field = fkField;
				} catch (SQLException e) {
					e.printStackTrace();
					continue;
				}
			} else {
				JFormattedTextField formattedField = new JFormattedTextField(this.getFormatByInt(columnModel.getType()));
				
				formattedField.setFocusLostBehavior(JFormattedTextField.PERSIST);
				field = formattedField;
			}

			field.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						add.doClick();
					}
				}
			});
			field.setPreferredSize(new Dimension(100, field.getPreferredSize().height));
			inputs.add(field);
			this.fields.put(columnModel.getName(), field);
			tmpBorder = field.getBorder();
		}
		final Border defaultBorder = tmpBorder;

		SpringUtilities.makeCompactGrid(inputs, this.fields.size(), 2, GAP, GAP, GAP, GAP);
		this.add(inputs);

		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean errors = false;
				for(String columnName : fields.keySet()) {
					JComponent genericField = fields.get(columnName);
					ColumnModel columnModel = model.getTableData().getTableModel().getColumns().get(columnName);
					if(genericField.getBorder() instanceof CompoundBorder) {
						genericField.setBorder(defaultBorder);
					}
					if(genericField instanceof JFormattedTextField) {
						JFormattedTextField field = (JFormattedTextField) genericField;
						try {
							field.commitEdit();
							if(!columnModel.isNullable() &&
									field.getValue().equals("")) {
								// TODO: Normal error handling
								throw new Exception();
							}
						} catch (Exception e1) {
							field.setBorder(new CompoundBorder(new LineBorder(Color.RED), field.getBorder()));
							errors = true;
						}
					} else if(genericField instanceof UnitableFkSelector) {
						UnitableFkSelector field = (UnitableFkSelector) genericField;
						if(field.getSelectedForeignKey() == null && !columnModel.isNullable()) {
							field.setBorder(new CompoundBorder(new LineBorder(Color.RED), field.getBorder()));
							errors = true;
						}
					}
				}
				if(errors) {
					return;
				}

				HashMap<String, Object> values = new HashMap<String, Object>();
				for(String columnName : fields.keySet()) {
					JComponent genericField = fields.get(columnName);
					if(genericField instanceof JFormattedTextField) {
						JFormattedTextField field = (JFormattedTextField) genericField;
						values.put(columnName, field.getValue());
					} else if(genericField instanceof UnitableFkSelector) {
						UnitableFkSelector field = (UnitableFkSelector) genericField;
						values.put(columnName, field.getSelectedForeignKey());
					}
				}

				try {
					model.getTableData().insertRow(values);
					int newRow = model.getTableData().getTableContents(false).getRow()-1;
					model.fireTableRowsInserted(newRow, newRow);
					model.getTableData().getTableContents(true).scheduleReExecution();
					
					for(String columnName : fields.keySet()) {
						JComponent genericField = fields.get(columnName);
						if(genericField instanceof JFormattedTextField) {
							JFormattedTextField field = (JFormattedTextField) genericField;
							field.setValue(null);
						} else if(genericField instanceof UnitableFkSelector) {
							UnitableFkSelector field = (UnitableFkSelector) genericField;
							field.setSelectedIndex(-1);
							field.hidePopup();
						}
					}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(UnitableAddForm.this, "Row can't be added: "+e1.getLocalizedMessage(), "Edit error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		this.add(add, BorderLayout.SOUTH);
	}

	private JFormattedTextField.AbstractFormatterFactory getFormatByInt(int type) {
		switch (type) {
			case Types.BIGINT:
			case Types.INTEGER:
				return new DefaultFormatterFactory(new NumberFormatter(NumberFormat.getIntegerInstance()));
			case Types.DECIMAL:
			case Types.NUMERIC:
			case Types.DOUBLE:
			case Types.FLOAT:
				return new DefaultFormatterFactory(new NumberFormatter(NumberFormat.getNumberInstance()));
			case Types.DATE:
				return new DefaultFormatterFactory(new DateFormatter(new SimpleDateFormat("yyyy-MM-dd")));
			case Types.TIME:
				return new DefaultFormatterFactory(new DateFormatter(new SimpleDateFormat("hh:mm:ss")));
			case Types.LONGVARCHAR:
			case Types.CHAR:
			case Types.VARCHAR:
				return new DefaultFormatterFactory(new DefaultFormatter());
			case Types.JAVA_OBJECT:
			case Types.NULL:
			case Types.BOOLEAN:
			default:
				throw new UnsupportedOperationException();
		}
	}
}
