package com.nevkontakte.unitable.client.reports;

import java.awt.*;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: aleks
 * Date: 20.05.11
 * Time: 18:31
 */
public class StudentsByTeacherMark extends BasicReport{
	public StudentsByTeacherMark(Window parent, Connection db) {
		super(parent, db);
	}

	@Override
	public String getReportName() {
		return "Students by teacher mark";
	}
}
