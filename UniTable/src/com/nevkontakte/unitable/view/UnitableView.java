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
	private final JScrollPane scroll;
	private final JTable table;
	private final UnitableViewModel model;


	public UnitableView(UnitableViewModel model) {
		this.model = model;
		this.table = new JTable(this.model);
		this.table.setAutoCreateRowSorter(true);
		this.scroll = new JScrollPane(this.table);

		// Configure GUI
		this.setLayout(new BorderLayout());
		this.add(this.scroll);
	}
}
