package com.nevkontakte.unitable.view;

import com.nevkontakte.unitable.model.ColumnModel;
import com.nevkontakte.unitable.model.JdbcTypeHelper;
import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.UnitableRowSet;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.Iterator;

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

	public UnitableViewModel(TableData tableData) {
		this.tableData = tableData;
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
		return this.tableData.getTableModel().getColumns().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			UnitableRowSet data = this.tableData.getTableContents(false);
			data.absolute(rowIndex + 1);
			return data.getObject(columnIndex + 1);
		} catch (SQLException e) {
			// TODO: handle nicely
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
			UnitableRowSet rows = this.tableData.getTableContents(false);
			rows.absolute(rowIndex + 1);
			rows.updateObject(columnIndex + 1, aValue);
			rows.updateRow();
			System.out.println(aValue);
		} catch (SQLException e) {
			// TODO: Report error
			e.printStackTrace();
		}
	}

	@Override
	public String getColumnName(int column) {
		Iterator<ColumnModel> i = this.tableData.getTableModel().getColumns().values().iterator();
		ColumnModel columnModel = null;
		for (int j = 0; j <= column; j++) {
			columnModel = i.next();
		}
		return columnModel.getName();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		String columnName = this.getColumnName(columnIndex);
		ColumnModel columnModel = this.tableData.getTableModel().getColumns().get(columnName);
		int columnType = columnModel.getType();
		Class<?> cls = JdbcTypeHelper.getClassByInt(columnType);
		if (cls == null) {
			cls = String.class;
		}
		return cls;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		String columnName = this.getColumnName(columnIndex);
		ColumnModel columnModel = this.tableData.getTableModel().getColumns().get(columnName);
		return !this.tableData.getTableModel().getPrimaryKeys().contains(columnModel);
	}

	public TableData getTableData() {
		return tableData;
	}
}
