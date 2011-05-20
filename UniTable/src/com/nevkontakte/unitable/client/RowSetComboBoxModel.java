package com.nevkontakte.unitable.client;

import com.nevkontakte.unitable.model.UnitableRowSet;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 20.05.11
 * Time: 15:55
 */
public class RowSetComboBoxModel extends DefaultComboBoxModel{
	public RowSetComboBoxModel(UnitableRowSet rowSet) throws SQLException {
		super();
		rowSet.executeOnce();
		if(rowSet.getType() != UnitableRowSet.TYPE_FORWARD_ONLY) {
			rowSet.beforeFirst();
		}
		while(rowSet.next()) {
			this.addElement(rowSet.getObject(1));
		}
	}
}
