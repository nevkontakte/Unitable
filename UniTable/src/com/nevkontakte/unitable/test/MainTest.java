package com.nevkontakte.unitable.test;

import com.nevkontakte.unitable.model.TableData;
import com.nevkontakte.unitable.model.TableModel;
import oracle.jdbc.OracleDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

/**
 * User: aleks
 * Date: 25.03.11
 * Time: 18:08
 */
public class MainTest {
	public static void main(String[] args) throws SQLException {
        Locale.setDefault(Locale.ENGLISH);
        DriverManager.registerDriver(new OracleDriver());
        String dburl = "jdbc:oracle:thin:@localhost:1521:XE";
        dburl = "jdbc:mysql://localhost/unitable";
        String user = "aleks";
        String password =  "mpwd2007";
		Connection connection = DriverManager.getConnection(dburl, user, password);
		TableModel model = new TableModel(connection, "people");
        TableData data = new TableData(model);
        System.out.println(data.getTableContents(false));
	}
}
