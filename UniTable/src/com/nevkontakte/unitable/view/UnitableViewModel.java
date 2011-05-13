package com.nevkontakte.unitable.view;

import com.nevkontakte.unitable.model.*;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 08.04.11
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 * To change this template use File | Settings | File Templates.
 */
public class UnitableViewModel extends AbstractTableModel {
	protected final TableData tableData;
	protected final ArrayList<ViewColumnModel> columns = new ArrayList<ViewColumnModel>();
	protected final DeleteViewColumnModel deleteColumn = new DeleteViewColumnModel();

	public UnitableViewModel(TableData tableData) throws SQLException {
		this.tableData = tableData;

		int i = 0;
		for(ColumnModel column : this.tableData.getTableModel().getColumns().values()) {
			if( (column.isMarked() && (!column.isHidden())) ||
					((!column.isMarked()) && (!this.tableData.getTableModel().getPrimaryKeys().contains(column)))) {
				DbViewColumnModel viewModel;
				ForeignKeyModel fk = this.tableData.getTableModel().getForeignKey(column);
				if(fk != null) {
					viewModel = new DbFkViewColumnModel(i, column, this.tableData);
				} else {
					viewModel = new DbViewColumnModel(i, column, this.tableData);
				}
				this.columns.add(viewModel);
			}
			i++;
		}
		this.columns.add(this.deleteColumn);
		//this.addTableModelListener(this.deleteColumn);
	}

	public int getRowCount() {
		try {
			return this.tableData.getRowCount();
		} catch (SQLException e) {
			// TODO: handle nicely
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return 0;
		}
	}

	public int getColumnCount() {
		return this.columns.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return this.columns.get(columnIndex).getValueAt(rowIndex);
	}

	public Object getFkValueAt(int rowIndex, int columnIndex) {
		ViewColumnModel column = this.columns.get(columnIndex);
		if(column instanceof DbFkViewColumnModel) {
			return ((DbFkViewColumnModel) column).getFkValueAt(rowIndex);
		}
		return column.getValueAt(rowIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		this.columns.get(columnIndex).setValueAt(aValue, rowIndex);
	}

	@Override
	public String getColumnName(int column) {
		return this.columns.get(column).getColumnName();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return this.columns.get(columnIndex).getColumnClass();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return this.columns.get(columnIndex).isCellEditable(rowIndex);
	}

	public TableData getTableData() {
		return tableData;
	}

	ViewColumnModel getColumnModel(int columnIndex) {
		return this.columns.get(columnIndex);
	}

	public void deleteMarked() throws SQLException {
		UnitableRowSet data = this.getTableData().getTableContents(false);
		Map<Integer, Boolean> selected = this.deleteColumn.getSelected();

		int correction = 0;
		for(Integer i : selected.keySet()) {
			if(selected.get(i) == true) {
				data.absolute(i+1-correction);
				data.deleteRow();
				correction++;
			}
		}
		
		this.deleteColumn.reset();
		this.getTableData().getTableContents(true).scheduleReExecution();
		this.fireTableChanged(null);
	}

	public void refreshRow(int row) throws SQLException {
		this.tableData.getTableContents(true).absolute(row+1);
		this.tableData.getTableContents(true).refreshRow();
	}

	static interface ViewColumnModel {
		public String getColumnName();
		public Class<?> getColumnClass();
		public Object getValueAt(int rowIndex);
		public void setValueAt(Object aValue, int rowIndex);
		public boolean isCellEditable(int rowIndex);
	}

	static class DbViewColumnModel implements ViewColumnModel {
		protected final int dataColumnIndex;
		protected final ColumnModel columnModel;
		protected final TableData tableData;

		public DbViewColumnModel(int dataColumnIndex, ColumnModel columnModel, TableData tableData) {
			this.dataColumnIndex = dataColumnIndex;
			this.columnModel = columnModel;
			this.tableData = tableData;
		}


		public String getColumnName() {
			return columnModel.getHumanName();
		}

		public Class<?> getColumnClass() {
			int columnType = columnModel.getType();
			Class<?> cls = JdbcTypeHelper.getClassByInt(columnType);
			if (cls == null) {
				cls = String.class;
			}
			return cls;
		}

		public Object getValueAt(int rowIndex) {
			try {
				UnitableRowSet data = this.tableData.getTableContents(false);
				data.absolute(rowIndex + 1);
				return data.getObject(dataColumnIndex + 1);
			} catch (SQLException e) {
				// TODO: handle nicely
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				return null;
			}
		}

		public void setValueAt(Object aValue, int rowIndex) {
			try {
				UnitableRowSet rows = this.tableData.getTableContents(false);
				rows.absolute(rowIndex + 1);
				rows.updateObject(this.dataColumnIndex + 1, aValue);
				rows.updateRow();
			} catch (SQLException e) {
				// TODO: Report error
				e.printStackTrace();
			}
		}

		public boolean isCellEditable(int rowIndex) {
			return true;
		}
	}

	static class DbFkViewColumnModel extends DbViewColumnModel{
		protected final ForeignKeyModel fk;

		public DbFkViewColumnModel(int dataColumnIndex, ColumnModel columnModel, TableData tableData) throws SQLException {
			super(dataColumnIndex, columnModel, tableData);
			this.fk = this.tableData.getTableModel().getForeignKey(columnModel);
		}

		public Class<?> getColumnClass() {
			return String.class;
		}

		public Object getFkValueAt(int rowIndex) {
			return this.tableData.getFkHumanValue(fk.getFkColumnName(), rowIndex);
		}

		public ForeignKeyModel getForeignKeyModel() {
			return this.fk;
		}
	}

	private static class DeleteViewColumnModel implements ViewColumnModel, TableModelListener {
		private HashMap <Integer, Boolean> markers = new HashMap<Integer, Boolean>();

		public String getColumnName() {
			return "Delete";
		}

		public Class<?> getColumnClass() {
			return Boolean.class;
		}

		public Object getValueAt(int rowIndex) {
			return this.markers.containsKey(rowIndex)?this.markers.get(rowIndex):false;
		}

		public void setValueAt(Object aValue, int rowIndex) {
			this.markers.put(rowIndex, (Boolean) aValue);
		}

		public boolean isCellEditable(int rowIndex) {
			return true;
		}

		public void tableChanged(TableModelEvent e) {
			if(e.getType() == TableModelEvent.DELETE) {
				this.markers.clear();
			}
		}

		public Map<Integer, Boolean> getSelected() {
			return this.markers;
		}

		public void reset() {
			this.markers.clear();
		}
	}
}
