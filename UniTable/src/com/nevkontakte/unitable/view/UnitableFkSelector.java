package com.nevkontakte.unitable.view;

import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.UnitableRowSet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 29.04.11
 * Time: 17:16
 */
public class UnitableFkSelector extends JComboBox {
	private FkSelectorModel model;
	private boolean isAutoCompleting = false;

	public UnitableFkSelector(TableData data) {
		model = new FkSelectorModel(data);
		this.setModel(model);
		final JTextField field = (JTextField) this.getEditor().getEditorComponent();
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				if(UnitableFkSelector.this.isShowing()) {
					showPopup();
				}
			}

			public void removeUpdate(DocumentEvent e) {
				if(UnitableFkSelector.this.isShowing()) {
					showPopup();
				}
			}

			public void changedUpdate(DocumentEvent e) {
				if(UnitableFkSelector.this.isShowing()) {
					showPopup();
				}
			}
		});
		AutoCompletionListener l = new AutoCompletionListener();
		field.getDocument().addUndoableEditListener(l);
		field.addKeyListener(l);
		this.setEditable(true);
	}

	private void autoComplete(int candidate) {
		if(this.isAutoCompleting()) {
			return;
		}
		this.setAutoCompleting(true);
		JTextField field = (JTextField) this.getEditor().getEditorComponent();
		int cursor = field.getCaretPosition();
		int oldSelection = this.getSelectedIndex();
		this.setSelectedIndex(candidate);
		getEditor().setItem(this.getSelectedItem());
		field.setSelectionStart(cursor);
		field.setSelectionEnd(field.getText().length());
		this.setAutoCompleting(false);
	}

	protected boolean isAutoCompleting() {
		return isAutoCompleting;
	}

	protected void setAutoCompleting(boolean autoCompleting) {
		isAutoCompleting = autoCompleting;
	}

	public Object getSelectedForeignKey() {
		Object selected = this.model.getSelectedItem();
		if(selected instanceof ListItem) {
			return ((ListItem) selected).getId();
		} else {
			return null;
		}
	}

	public void setSelectedForeignKey(Object key) {
		for(int i = 0; i < this.model.getSize(); i++) {
			boolean equals;
			Integer id = this.model.getElementAt(i).getId();
			if(key instanceof BigDecimal) {
				equals = BigDecimal.valueOf(id).compareTo((BigDecimal) key) == 0;
			} else {
				equals = key.equals(id);
			}
			if(equals) {
				this.setSelectedIndex(i);
				JTextField field = (JTextField) this.getEditor().getEditorComponent();
				field.setCaretPosition(field.getText().length());
				return;
			}
		}
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

		public ListItem getElementAt(int index) {
			return this.items.get(index);
		}

		public int getAutoCompleteCandidate(String partial) {
			for(int i = 0; i < this.items.size(); i++) {
				if(this.items.get(i).getTitle().toLowerCase().startsWith(partial.toLowerCase())) {
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

	private class AutoCompletionListener extends KeyAdapter implements UndoableEditListener {
		int candidate = -1;
		public void undoableEditHappened(UndoableEditEvent e) {
			// Prepare auto completion
			if(!isAutoCompleting()) {
				JTextField field = (JTextField) getEditor().getEditorComponent();
				this.candidate = model.getAutoCompleteCandidate(field.getText());
				if(this.candidate == -1) {
					e.getEdit().undo();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			JTextField field = (JTextField) getEditor().getEditorComponent();
			if(this.candidate != -1 && field.getFont().canDisplay(e.getKeyCode())) {
				autoComplete(this.candidate);
			}

			if(isPopupVisible()) {
				e.consume();
			}
		}
	}
}
