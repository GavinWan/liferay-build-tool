
package com.liferay.pb.tasks.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.liferay.pb.Constants;
import com.liferay.pb.tasks.AppServerSetup;
import com.liferay.pb.tools.PropertiesUtil;
import com.liferay.pb.tools.VelocityUtil;
import com.liferay.pb.tools.ZipUtil;

public class TomcatBundleSetupImpl implements AppServerSetup {

	@Override
	public void performTask() {

		if (!PropertiesUtil.getBoolean(Constants.APP_SERVER_INSTALL)) {

			return;
		}
		String systemUser = System.getProperty("user.name");

		try {
			// unpack
			log.info("unpack tomcat bundle ... ");
			String destination =
				PropertiesUtil.getString(Constants.APP_SERVER_DIR)
					+ File.separator;

			String appserverPackage =
				PropertiesUtil.getString(Constants.APP_SERVER_PACKAGE_LOCATION);

			File file =
				new File(getClass()
					.getClassLoader().getResource(appserverPackage).getFile());

			// zipname liferay-portal-tomcat-6.0-ee-sp1.zip
			// root folder liferay-portal-6.0-ee-sp1 in bundle zip
			String skipRootFolder =
				StringUtils.stripFilenameExtension(file.getName()).replace(
					"tomcat-", "");

			ZipUtil.unzip(destination, file, skipRootFolder);

			log.info("config app.server.properties ... ");
			// deploy app.server.systemUser.properties
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(
				"server_dir",
				PropertiesUtil.getString(Constants.APP_SERVER_DIR));

			// portal src
			File appServerProperties =
				new File(PropertiesUtil.getString(Constants.SOURCE_PORTAL_DIR)
					+ File.separator
					+ "app.server." + systemUser + ".properties");

			VelocityUtil.mergeTemplate(
				"/liferay/app.server.properties.vm", map, appServerProperties);

			// plugins sdk build.properties
			log.info("config build.properties ... ");
			File buildProperties =
				new File(PropertiesUtil.getString(Constants.SOURCE_PLUGINS_DIR)
					+ File.separator
					+ "build." + systemUser + ".properties");

			VelocityUtil.mergeTemplate(
				"/liferay/build.properties.vm", map, buildProperties);

		}
		catch (Exception e) {
			log.error(e);
		}
	}
	private static Log log = LogFactory.getLog(HudsonConfigureImpl.class);
}
