package com.nevkontakte.unitable.view;

import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.UnitableRowSet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 29.04.11
 * Time: 17:16
 */
public class UnitableFkSelector extends JComboBox {
	private TableData data;
	private FkSelectorModel model;
	private boolean isAutoCompleting = false;

	public UnitableFkSelector(TableData data) {
		this.data = data;
		this.setEditable(true);
		model = new FkSelectorModel(data);
		this.setModel(model);
		final JTextField field = (JTextField) this.getEditor().getEditorComponent();
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				showPopup();
			}

			public void removeUpdate(DocumentEvent e) {
				showPopup();
			}

			public void changedUpdate(DocumentEvent e) {
				showPopup();
			}
		});
		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(field.getFont().canDisplay(e.getKeyCode())) {
					autoComplete();
				}
			}
		});
	}

	private void autoComplete() {
		if(this.isAutoCompleting()) {
			return;
		}
		this.setAutoCompleting(true);
		JTextField field = (JTextField) this.getEditor().getEditorComponent();
		String fieldText = field.getText();
		if(fieldText.length() != 0) {
			int select = this.model.getAutoCompleteCandidate(fieldText);
			if(select != -1) {
				int cursor = field.getCaretPosition();
				System.out.println(field.getText());
				this.setSelectedIndex(select);
				System.out.println(field.getText());
				getEditor().setItem(this.getSelectedItem());
				System.out.println(field.getText());
				field.setSelectionStart(cursor);
				field.setSelectionEnd(field.getText().length());
				System.out.println(field.getText());
				//field.setCaretPosition(cursor);
//				System.out.println(cursor);
//				System.out.println(field.getText().length());
			}
		}
		this.setAutoCompleting(false);
	}

	protected boolean isAutoCompleting() {
		return isAutoCompleting;
	}

	protected void setAutoCompleting(boolean autoCompleting) {
		isAutoCompleting = autoCompleting;
	}

	private static class FkSelectorModel extends DefaultComboBoxModel{
		private LinkedList<ListItem> items = new LinkedList<ListItem>();

		private FkSelectorModel(TableData data) {
			try {
				UnitableRowSet rows = data.getTableContents(false);
				rows.beforeFirst();
				while (rows.next()) {
					items.add(new ListItem(data.getHumanValue(rows.getRow()), rows.getInt(data.getTableModel().getPrimaryKeys().get(0).getName())));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public int getSize() {
			return this.items.size();
		}

		public Object getElementAt(int index) {
			return this.items.get(index);
		}

		public int getAutoCompleteCandidate(String partial) {
			for(int i = 0; i < this.items.size(); i++) {
				if(this.items.get(i).getTitle().startsWith(partial)) {
					return i;
				}
			}
			return -1;
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
			return this.title;
		}
	}
}
