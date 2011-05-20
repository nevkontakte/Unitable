package com.nevkontakte.unitable.model;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.Date;
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
		for(Field f: fields) {
			try {
				// Get field name
				String name = f.getName();

				// Get field value
				Integer value = (Integer) f.get(null);

				// Add to map
				intToStr.put(value, name);
			} catch (IllegalAccessException e) {
			}
		}
	}

	public static String getStringByInt(int type) {
		return intToStr.get(type);
	}

	public static Class<?> getClassByInt(int type) {
		switch (type) {
			case Types.BIGINT:
				return BigInteger.class;
			case Types.BOOLEAN:
				return Boolean.class;
			case Types.CHAR:
				return Character.class;
			case Types.DECIMAL:
			case Types.NUMERIC:
				return BigDecimal.class;
			case Types.DOUBLE:
				return Double.class;
			case Types.FLOAT:
				return Float.class;
			case Types.INTEGER:
				return Integer.class;
			case Types.JAVA_OBJECT:
				return Object.class;
			case Types.NULL:
				return null;
			case Types.DATE:
			case Types.TIME:
				return Date.class;
			case Types.LONGVARCHAR:
			case Types.VARCHAR:
				return String.class;
			default:
				return null;
		}
	}
}
