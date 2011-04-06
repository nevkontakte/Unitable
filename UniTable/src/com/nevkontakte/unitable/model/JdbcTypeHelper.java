package com.nevkontakte.unitable.model;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * User: aleks
 * Date: 25.03.11
 * Time: 20:00
 */
public class JdbcTypeHelper {
	private final static HashMap<Integer, String> intToStr = new HashMap<Integer, String>();

	static {
		Field[] fields = java.sql.Types.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				// Get field name
				String name = fields[i].getName();

				// Get field value
				Integer value = (Integer) fields[i].get(null);

				// Add to map
				intToStr.put(value, name);
			} catch (IllegalAccessException e) {
			}
		}
	}

	public static String getStringByInt(int type) {
		return intToStr.get(type);
	}
}
