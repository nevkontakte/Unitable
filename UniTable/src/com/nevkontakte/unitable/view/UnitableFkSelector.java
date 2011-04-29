package com.nevkontakte.unitable.view;

import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.UnitableRowSet;

import javax.swing.*;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 29.04.11
 * Time: 17:16
 */
public class UnitableFkSelector extends JComboBox{
	private TableData data;

	public UnitableFkSelector(TableData data) {
		this.data = data;
		this.setModel(new FkSelectorModel(data));
	}

	private static class FkSelectorModel extends DefaultComboBoxModel{
		private LinkedList<ListItem> items = new LinkedList<ListItem>();

		private FkSelectorModel(TableData data) {
			try {
				UnitableRowSet rows = data.getTableContents(false);
				rows.beforeFirst();
				while (rows.next()) {
					items.add(new ListItem(data.getHumanValue(rows.getRow()), rows.getInt(data.getTableModel().getPrimaryKeys().get(0).getName())));
					System.out.println(data.getHumanValue(rows.getRow()));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println(this.items);
		}

		public int getSize() {
			return this.items.size();
		}

		public Object getElementAt(int index) {
			return this.items.get(index).getTitle();
		}
	}

	public static class ListItem {
		private String title;
		private int id;

		public ListItem(String title, int id) {
			this.title = title;
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return String.format("<%d, %s>", this.id, this.title);
		}
	}
}
