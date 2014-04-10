/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Oct 4, 2013
 */
public interface IFixtureTestBase {

	public abstract void setupData(RepositoryContainer repos)
			throws TestEnvironmentConfigException;

	public abstract void createRepositoryContainer()
			throws TestEnvironmentConfigException;

	public abstract void cleanupEverything();

}