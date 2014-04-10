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
public abstract class RestTestBase {
	
	private HttpSession session;

	@BeforeMethod
	public void before() throws TestEnvironmentConfigException {
		session = new HttpSessionFactory().createHttpSession(RestApiConfiguration.getRestApi());
	}

	@AfterMethod
	public void afterCliTestBase(){
		RhscCleanerTool.cleanup();
		session.stop();
	}
}
