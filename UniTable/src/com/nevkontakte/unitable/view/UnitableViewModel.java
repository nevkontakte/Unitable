package com.nevkontakte.unitable.view;

import com.nevkontakte.unitable.model.ColumnModel;
import com.nevkontakte.unitable.model.JdbcTypeHelper;
import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.UnitableRowSet;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.ArrayList;

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

	public UnitableViewModel(TableData tableData) {
		this.tableData = tableData;

		int i = 0;
		for(ColumnModel column : this.tableData.getTableModel().getColumns().values()) {
			if(!column.isHidden()) {
				this.columns.add(new DataViewColumnModel(i, column, this.tableData));
			}
			i++;
		}
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

	private static interface ViewColumnModel {
		public String getColumnName();
		public Class<?> getColumnClass();
		public Object getValueAt(int rowIndex);
		public void setValueAt(Object aValue, int rowIndex);
		public boolean isCellEditable(int rowIndex);
	}

	private static class DataViewColumnModel implements ViewColumnModel {
		final int dataColumnIndex;
		final ColumnModel columnModel;
		final TableData tableData;

		public DataViewColumnModel(int dataColumnIndex, ColumnModel columnModel, TableData tableData) {
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
}
