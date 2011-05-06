package com.nevkontakte.unitable.view;

import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.TableModel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 08.04.11
 * Time: 13:09
 * To change this template use File | Settings | File Templates.
 */
public class UnitableView extends JPanel {
	// TODO Add support of date editing
	private final JScrollPane scroll;
	private final JTable table;
	private final UnitableViewModel model;
	private final UnitableAddForm addForm;


	public UnitableView(UnitableViewModel model) {
		// Init components
		this.model = model;
		this.table = new JTable(this.model);
		this.scroll = new JScrollPane(this.table);
		this.addForm = new UnitableAddForm(this.model);
		JPanel buttons = new JPanel(new FlowLayout());
		JButton deleteButton = new JButton("Delete");

		// Configure components
		this.table.setAutoCreateRowSorter(true);
		this.table.setDefaultRenderer(UnitableViewModel.DbFkViewColumnModel.class, new UnitableFkRenderer());
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UnitableView.this.model.deleteMarked();
				} catch (SQLException e1) {
					// TODO: Nice handling
					e1.printStackTrace();
				}
			}
		});

		// Configure GUI
		this.setLayout(new BorderLayout());
		this.add(this.scroll);
		this.add(this.addForm, BorderLayout.SOUTH);
		buttons.add(deleteButton);
		this.add(buttons, BorderLayout.NORTH);

		// Configure preferred column widths
		TableColumnModel columnModel = this.table.getColumnModel();
		TableCellRenderer defaultRenderer = this.table.getTableHeader().getDefaultRenderer();
		int margin = columnModel.getColumnMargin();
		for(int i = 0; i < columnModel.getColumnCount(); i++) {
			TableColumn column = columnModel.getColumn(i);
			TableCellRenderer renderer = column.getHeaderRenderer();
			if(renderer == null) {
				renderer = defaultRenderer;
			}
			if(renderer != null) {
				Component c = renderer.getTableCellRendererComponent(this.table, column.getHeaderValue(), false, false, -1, i);
				int width = c.getPreferredSize().width;
				column.setPreferredWidth(width+margin);
			}
		}

		// Configure cell renderers and editors
		UnitableFkRenderer fkRenderer = new UnitableFkRenderer();
		for(int i = 0; i < columnModel.getColumnCount(); i++) {
			if(this.model.getColumnModel(i) instanceof UnitableViewModel.DbFkViewColumnModel) {
				columnModel.getColumn(i).setCellRenderer(fkRenderer);
				UnitableViewModel.DbFkViewColumnModel viewColumnModel = (UnitableViewModel.DbFkViewColumnModel) this.model.getColumnModel(i);
				try {
					TableModel fkTableModel = TableModel.get(this.model.getTableData().getTableModel().getDb(), viewColumnModel.getForeignKeyModel().getPkTableName());
					columnModel.getColumn(i).setCellEditor(new FkCellEditor(new TableData(fkTableModel)));
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}
		this.table.setPreferredScrollableViewportSize(new Dimension(this.table.getPreferredSize().width, this.table.getPreferredScrollableViewportSize().height));
		this.table.setAutoCreateColumnsFromModel(false);
	}

	public class UnitableFkRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			UnitableViewModel.ViewColumnModel columnModel = model.getColumnModel(column);
			if(columnModel instanceof UnitableViewModel.DbFkViewColumnModel) {
				value = ((UnitableViewModel.DbFkViewColumnModel) columnModel).getFkValueAt(row);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}

	private class FkCellEditor extends AbstractCellEditor implements TableCellEditor{
		UnitableFkSelector field;

		private FkCellEditor(TableData data) {
			this.field = new UnitableFkSelector(data);
			this.field.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						fireEditingStopped();
						try {
							model.getTableData().getTableContents(true).scheduleReExecution();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						if (field.isPopupVisible()) {
							fireEditingCanceled();
						}
					}
				}
			});
		}

		public Object getCellEditorValue() {
			return this.field.getSelectedForeignKey();
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			this.field.setSelectedForeignKey(value);
			return this.field;
		}
	}
}
