package com.nevkontakte.unitable.view;

import javax.swing.*;
import java.awt.*;

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

		// Configure components
		this.table.setAutoCreateRowSorter(true);

		// Configure GUI
		this.setLayout(new BorderLayout());
		this.add(this.scroll);
		this.add(this.addForm, BorderLayout.SOUTH);
	}
}
