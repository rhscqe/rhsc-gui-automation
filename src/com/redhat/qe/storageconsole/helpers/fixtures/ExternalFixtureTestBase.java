/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import org.testng.annotations.AfterMethod;

import com.redhat.qe.storageconsole.helpers.RhscCleanerTool;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Oct 4, 2013
 */
public abstract class ExternalFixtureTestBase extends SahiTestBase {

	public abstract void setupData(RepositoryContainer repos) throws TestEnvironmentConfigException;
	
	@AfterMethod
	public void afterCliTestBase() {
		RhscCleanerTool.cleanup();
	}

}