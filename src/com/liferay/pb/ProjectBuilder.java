
package com.liferay.pb;

import org.apache.velocity.app.Velocity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

import com.liferay.pb.tasks.AppServerSetup;
import com.liferay.pb.tasks.QualityMetrics;
import com.liferay.pb.tasks.RepositoryStructure;
import com.liferay.pb.tools.PropertiesUtil;

public class ProjectBuilder {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ApplicationContext ac =
			new ClassPathXmlApplicationContext("META-INF/spring.xml");

		init();

		// RepositoryStructure
		RepositoryStructure repositoryStructure =
			(RepositoryStructure) ac.getBean(RepositoryStructure.class
				.getName());

		repositoryStructure.performTask();

		// AppServerSetup
		AppServerSetup appServerSetup =
			(AppServerSetup) ac.getBean(AppServerSetup.class.getName());
		
		appServerSetup.performTask();
		
		// QualityMetrics
		QualityMetrics qualityMetrics =
			(QualityMetrics) ac.getBean(QualityMetrics.class.getName());
		
		qualityMetrics.performTask();

	}

	public static void init() throws Exception{
		
		//Property
		PropertiesUtil.load("config.properties");
		
		//Velocity
		Velocity.init();
		
		//setup svn
		SVNRepositoryFactoryImpl.setup();
		DAVRepositoryFactory.setup();

	}

}
