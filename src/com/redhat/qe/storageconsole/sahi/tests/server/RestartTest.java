/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.server;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.helpers.CannotStartConnectException;
import com.redhat.qe.storageconsole.helpers.Duration;
import com.redhat.qe.storageconsole.helpers.Times;
import com.redhat.qe.storageconsole.helpers.cli.Ssher;
import com.redhat.qe.storageconsole.sahi.base.SSHClient;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.RhscHostTasks;

/**
 * @author dustin 
 * Jan 24, 2013
 */
public class RestartTest extends SahiTestBase{
	final protected static Logger LOG = Logger.getLogger(RestartTest.class.getName());
	private static final int NUM_ATTEMPTS = 50;
	private RhscHostTasks rhscHostTasks;
	private String url;
	
	@BeforeMethod
	public void setup() throws CannotStartConnectException{
		rhscHostTasks = new RhscHostTasks();
		navigateToBlankScreen();
	}
	
	@Test
	public void testRestart() throws InterruptedException, CannotStartConnectException{
		//test step
		rhscHostTasks.restart();
		assertTrue(rhscHostTasks.waitUntilSeverStopsResponding()); //restart is not a blocking command
		
		//verify
		assertTrue(waitForWebServerToStart(), "timed out waiting for server to start");
		browser.navigateTo(getUrl(),true);
		assertTrue(isLoginPageDisplayed(), "server did not restart");
	}
	@Test
	public void testJboss() throws InterruptedException, CannotStartConnectException{
		//test step
		rhscHostTasks.restartJboss();
		
		//verify
		assertTrue(waitForWebServerToStart(), "timed out waiting for server to start");
		browser.navigateTo(getUrl(),true);
		assertTrue(isLoginPageDisplayed(), "server did not restart");
	}
	
	


	/**
	 * 
	 */
	private void navigateToBlankScreen() {
		browser.navigateTo("http://sahi.example.com/_s_/dyn/Driver_initialized");
	}

	/**
	 * 
	 */
	private boolean waitForWebServerToStart() {
		for(int index : new Times(NUM_ATTEMPTS)){
		  LOG.log(Level.FINEST, String.format("waiting for server to start attempt %s/%s", index,NUM_ATTEMPTS));
		  try{
			String curl = rhscHostTasks.curl(getUrl());
			if(curl != null && curl.contains("HTTP/1.1 200 OK")){
				Thread.sleep(Duration.TEN_SECONDS.toMilliseconds());
				LOG.log(Level.FINEST, "server started");
				return true;
			}
		  }catch(Exception e){
			//do nothing
		  }
		  Duration.TEN_SECONDS.sleep();
		}
		return false;
	}

	/**
	 * @return
	 */
	private boolean isLoginPageDisplayed() {
		return browser.div("Red Hat Storage").exists();
	}

	
	@Override
	protected String getUrl() {
		url = url == null ? getUrlFromConfiguration() : url;
		return url;
	}

	/**
	 * @return
	 */
	private String getUrlFromConfiguration() {
		try {
			return super.getUrl();
		} catch (Exception e) {
			throw new RuntimeException("can't get configured url for rhsc");
		}
	}

}
