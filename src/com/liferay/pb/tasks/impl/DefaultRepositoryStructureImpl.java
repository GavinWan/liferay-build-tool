
package com.liferay.pb.tasks.impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.liferay.pb.Constants;
import com.liferay.pb.tasks.RepositoryStructure;
import com.liferay.pb.tools.PropertiesUtil;
import com.liferay.pb.tools.ZipUtil;

public class DefaultRepositoryStructureImpl implements RepositoryStructure {

	public void performTask() {

		try {
			log.info("createStructure");
			
			createStructure();

			// portal source
			if (PropertiesUtil.getBoolean(Constants.SOURCE_PORTAL_INSTALL)) {
				log.info("portal source ... ");
				installPortalSource();
			}

			// plugins source
			if (PropertiesUtil.getBoolean(Constants.SOURCE_PLUGINS_INSTALL)) {
				log.info("plugins source ... ");
				installPluginsSource();
			}

			// import to svn
			if (PropertiesUtil.getBoolean(Constants.PROJECT_SHARE)) {
				log.info("import to SVN ... ");
				importToSVN();
			}

		}
		catch (Exception e) {
			log.error(e);
		}
	}

	protected void createStructure()
		throws Exception {

		String destination =
			PropertiesUtil.getString(Constants.PROJECT_DIR) + File.separator;

		String skeleton =
			PropertiesUtil.getString(Constants.REPOSITORY_SKELETON);

		ZipUtil.unzip(destination, new File(getClass()
			.getClassLoader().getResource(skeleton).getFile()));
	}

	protected void installPortalSource()
		throws Exception {

		if (FILE_SYSTEM.equals(PropertiesUtil
			.getString(Constants.SOURCE_PORTAL_PROVIDER))) {
			String destination =
				PropertiesUtil.getString(Constants.SOURCE_PORTAL_DIR)
					+ File.separator;

			String skeleton =
				PropertiesUtil
					.getString(Constants.SOURCE_PORTAL_PACKAGE_LOCATION);

			ZipUtil.unzip(destination, new File(getClass()
				.getClassLoader().getResource(skeleton).getFile()));
		}
		else {
			String svnurl =
				PropertiesUtil.getString(Constants.SOURCE_PORTAL_SVN_URL);
			String name =
				PropertiesUtil.getString(Constants.SOURCE_PORTAL_SVN_NAME);
			String password =
				PropertiesUtil.getString(Constants.SOURCE_PORTAL_SVN_PASSWORD);
			String dest = PropertiesUtil.getString(Constants.SOURCE_PORTAL_DIR);

			SVNURL repositoryURL = SVNURL.parseURIEncoded(svnurl);

			SVNClientManager ourClientManager =
				SVNClientManager.newInstance(
					SVNWCUtil.createDefaultOptions(true), name, password);

			SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
			updateClient.setIgnoreExternals(false);

			updateClient.doCheckout(
				repositoryURL, new File(dest), SVNRevision.UNDEFINED,
				SVNRevision.HEAD, SVNDepth.INFINITY, true);
		}
	}

	protected void installPluginsSource()
		throws Exception {

		String destination =
			PropertiesUtil.getString(Constants.SOURCE_PLUGINS_DIR)
				+ File.separator;

		String skeleton =
			PropertiesUtil.getString(Constants.SOURCE_PLUGINS_PACKAGE_LOCATION);

		ZipUtil.unzip(destination, new File(getClass()
			.getClassLoader().getResource(skeleton).getFile()));
	}

	protected void importToSVN()
		throws Exception {

		String name =
			PropertiesUtil.getString(Constants.PROJECT_SHARE_SVN_NAME);
		String password =
			PropertiesUtil.getString(Constants.PROJECT_SHARE_SVN_PASSWORD);
		String url = PropertiesUtil.getString(Constants.PROJECT_SHARE_SVN_URL);

		ISVNAuthenticationManager authManager =
			SVNWCUtil.createDefaultAuthenticationManager(name, password);

		SVNURL repositoryURL = SVNURL.parseURIEncoded(url);
		SVNCommitClient commitClient = new SVNCommitClient(authManager, null);

		//import
		String sourceRepository =
			PropertiesUtil.getString(Constants.PROJECT_SHARE_SVN_LOCAL_REPOSITORY);
		commitClient.doImport(
			new File(sourceRepository), repositoryURL, "init import", null,
			true, false, SVNDepth.INFINITY);

	}

	public static String FILE_SYSTEM = "fileSystem";
	public static String SVN = "svn";

	private static Log log = LogFactory
		.getLog(DefaultRepositoryStructureImpl.class);

}
