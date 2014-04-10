/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;


import org.calgb.test.performance.HttpSession;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.redhat.qe.helpers.rest.HttpSessionFactory;
import com.redhat.qe.storageconsole.helpers.RhscCleanerTool;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Oct 4, 2013
 */
public abstract class RestFixtureTestBase extends ExternalFixtureTestBase{
	
	
	
	private HttpSession session;

	@BeforeMethod
	public void setupRestSession() throws TestEnvironmentConfigException {
		session = new HttpSessionFactory().createHttpSession(RestApiConfiguration.getRestApi());
		RepositoryContainer repos = RepositoryContainer.getRepositoryContainer(session);
		setupData(repos);
		
	}
	@AfterMethod(alwaysRun=true)
	public void afterRestFixtureTestBase(){
		RhscCleanerTool.cleanup();
//		session.stop();
	}
	public HttpSession getSession() {
		return this.session;
	}
	
	
}
