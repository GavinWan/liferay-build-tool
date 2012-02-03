
package com.liferay.pb.tasks.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liferay.pb.Constants;
import com.liferay.pb.tasks.QualityMetrics;
import com.liferay.pb.tools.PropertiesUtil;
import com.liferay.pb.tools.VelocityUtil;
import com.liferay.pb.tools.ZipUtil;

public class HudsonConfigureImpl implements QualityMetrics {

	public void performTask() {

		if (!PropertiesUtil.getBoolean(Constants.QUALITY_METRICS_SETUP)) {
			return;
		}

		try {
			// unpack
			log.info("unpack Hudson server");
			String destination =
				PropertiesUtil.getString(Constants.QUALITY_METRICE_DIR)
				+ File.separator;

			String skeleton =
				PropertiesUtil
					.getString(Constants.QUALITY_METRICS_PACKAGE_LOCATION);

			ZipUtil.unzip(destination, new File(getClass()
				.getClassLoader().getResource(skeleton).getFile()));
			
			String hudsonHome = PropertiesUtil.getString(HUDSON_HOME);
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("hudson_home", hudsonHome);
			map.put("svn_url", PropertiesUtil.getString(HUDSON_SVN_URL));
			map.put("ant_task", PropertiesUtil.getString(HUDSON_ANY_TASK));

			// configuration conf\Catalina\localhost
			log.info("config husdon home ... ");
			File hudsonContext =
				new File(PropertiesUtil.getString(Constants.QUALITY_METRICE_DIR)
					+ "/conf/Catalina/localhost/hudson.xml");

			VelocityUtil.mergeTemplate(
				"/hudson/hudson.xml.vm", map, hudsonContext);

			// job config
			log.info("job config ... ");
			String projectName = PropertiesUtil.getString(Constants.PROJECT_NAME);
			File jobConfig =
				new File(hudsonHome + "/jobs/" + projectName + "/config.xml");

			VelocityUtil.mergeTemplate(
				"/hudson/config.xml.vm", map, jobConfig);

		}
		catch (Exception e) {
			log.error(e);
		}

	}
	
	public static String HUDSON_HOME = "quality.metrics.hudson.home";
	
	public static String HUDSON_SVN_URL = "quality.metrics.hudson.svn.url";
	
	public static String HUDSON_ANY_TASK = "quality.metrics.hudson.ant.task";
	
	private static Log log = LogFactory.getLog(HudsonConfigureImpl.class);

}
