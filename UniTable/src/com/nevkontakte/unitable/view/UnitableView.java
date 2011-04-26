package com.nevkontakte.unitable.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		this.table = new JTable(this.model) {
			UnitableRenderer renderer = new UnitableRenderer();

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				//return super.getCellRenderer(row, column);
				return this.renderer;
			}
		};
		this.scroll = new JScrollPane(this.table);
		this.addForm = new UnitableAddForm(this.model);
		JPanel buttons = new JPanel(new FlowLayout());
		JButton deleteButton = new JButton("Delete");

		// Configure components
		this.table.setAutoCreateRowSorter(true);
		this.table.setDefaultRenderer(UnitableViewModel.DbFkViewColumnModel.class, new UnitableRenderer());
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
		this.table.setPreferredScrollableViewportSize(new Dimension(this.table.getPreferredSize().width, this.table.getPreferredScrollableViewportSize().height));
		this.table.setAutoCreateColumnsFromModel(false);
	}

	public class UnitableRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			UnitableViewModel.ViewColumnModel columnModel = model.getColumnModel(column);
			if(columnModel instanceof UnitableViewModel.DbFkViewColumnModel) {
				value = ((UnitableViewModel.DbFkViewColumnModel) columnModel).getFkValueAt(row);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
}
