package com.nevkontakte.unitable.client.reports;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 19.05.11
 * Time: 21:31
 */
public class ProgressBarDialog extends JDialog{
	public ProgressBarDialog(Window owner, String title) {
		super(owner, title);
		JProgressBar progress = new JProgressBar(0, 100);
		progress.setIndeterminate(true);
		this.add(progress);
		this.setPreferredSize(new Dimension(250, this.getPreferredSize().height));
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(owner);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
}
