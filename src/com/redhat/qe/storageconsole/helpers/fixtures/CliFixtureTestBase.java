/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;


import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.testng.annotations.BeforeMethod;

import com.redhat.qe.storageconsole.helpers.CannotStartConnectException;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Oct 4, 2013
 */
public abstract class CliFixtureTestBase extends ExternalFixtureTestBase {
	
	
	@BeforeMethod
	public void createSession() throws TestEnvironmentConfigException {
		ShellSession session = new ShellSession();
		session.start(ShellConfiguration.getConfiguration());
		try{
			RepositoryContainer repos = RepositoryContainer.getRepositoryContainer(session.getShell());
			setupData(repos);
		}finally{
			session.stop();
			
		}
		
	}
}
