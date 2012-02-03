
package com.liferay.pb.tools;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class VelocityUtil {

	public static void mergeTemplate(
		String templateLocation, Map<String, Object> map, File file)
		throws Exception {

		FileUtils.writeStringToFile(file, VelocityEngineUtils
			.mergeTemplateIntoString(
				_velocityEngine, templateLocation, _encoding, map));
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {

		_velocityEngine = velocityEngine;
	}

	public void setEncoding(String encoding) {

		_encoding = encoding;
	}

	private static VelocityEngine _velocityEngine;
	private static String _encoding;
}
