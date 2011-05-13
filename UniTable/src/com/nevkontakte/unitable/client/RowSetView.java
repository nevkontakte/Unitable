package com.nevkontakte.unitable.client;

import com.nevkontakte.unitable.model.UnitableRowSet;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 13.05.11
 * Time: 18:16
 */
public class RowSetView extends JPanel{
	private JTable table = new JTable();
	public RowSetView(UnitableRowSet rowSet) {
		this.setLayout(new BorderLayout());
		this.table.setModel(new RowSetViewModel(rowSet));
		this.table.getTableHeader().setReorderingAllowed(false);
		this.add(new JScrollPane(table));
	}

	private class RowSetViewModel extends AbstractTableModel implements TableModel {
		private UnitableRowSet data;

		private RowSetViewModel(UnitableRowSet data) {
			this.data = data;
		}

		public int getRowCount() {
			try {
				return data.getRowCount();
			} catch (SQLException e) {
				// TODO: Error handling
				e.printStackTrace();
				return 0;
			}
		}

		public int getColumnCount() {
			try {
				return data.getMetaData().getColumnCount();
			} catch (SQLException e) {
				// TODO: Error handling
				e.printStackTrace();
				return 0;
			}
		}

		public String getColumnName(int columnIndex) {
			try {
				return data.getMetaData().getColumnName(columnIndex+1);
			} catch (SQLException e) {
				// TODO: Error handling
				e.printStackTrace();
				return "";
			}
		}

		public Class<?> getColumnClass(int columnIndex) {
			try {
				this.data.absolute(1);
				return this.data.getObject(columnIndex+1).getClass();
			} catch (SQLException e) {
			}
			return String.class;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			try {
				this.data.absolute(rowIndex+1);
				return this.data.getObject(columnIndex+1);
			} catch (SQLException e) {
			}
			return null;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}
	}
}
