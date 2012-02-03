
package com.liferay.pb.tools;

import org.apache.commons.configuration.PropertiesConfiguration;

public class PropertiesUtil {

	public static void load(String properties)
		throws Exception {

		configuration = new PropertiesConfiguration(properties);
	}

	public static String getString(String key) {

		return configuration.getString(key);
	}

	public static boolean getBoolean(String key) {

		return configuration.getBoolean(key);
	}

	public static void set(String key, Object value)
		throws Exception {

		configuration.setProperty(key, value);
		configuration.save();
	}

	private static PropertiesConfiguration configuration =
		new PropertiesConfiguration();
}
