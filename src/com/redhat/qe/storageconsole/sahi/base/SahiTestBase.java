package com.redhat.qe.storageconsole.sahi.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import net.sf.sahi.client.Browser;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.storageconsole.helpers.FileUtil;
import com.redhat.qe.storageconsole.sahi.tasks.ReportEngineListener;
import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiLoginLogoutTasks;
import com.redhat.qe.storageconsole.sahi.tests.LoginLogoutTest;
import com.redhat.qe.storageconsole.te.TestEnvironment;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Jul 26, 2012
 */
public class SahiTestBase{
	protected static Logger _logger = Logger.getLogger(SahiTestBase.class.getName());


	//TODO WARNING: theses needs to bechanged to an instance variable.
	// i know we are single threaded, but there are some methods that are using this in an unsafe way.
	public static StorageBrowser browser = null;	//Sahi object, uses to run browser actions
	private static final String loggerConfigLoc = "resources/logger.properties"; //Location of JUL logger file
	private static boolean httpEnabled = false;
	
	/*
	 * Loading JUL logger configurations from the file 'loggerConfigLoc'
	 */

	static {
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream(loggerConfigLoc));
			 _logger.log(Level.INFO, "Logger properties has loaded successfully, File Location: "+loggerConfigLoc);
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.log(Level.SEVERE, "Exception while loading properties file! ", ex);
		}
	}
	
	/*
	 * Loading Sahi configuration, XML Test Environment map and opens browser
	 */
	@BeforeMethod(alwaysRun=true)
	public void beforeTest() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		if(getTestEnvironment().getBrowser().isReopenForEachTest())		
			startBrowser();
	}
	
	@BeforeSuite(alwaysRun=true)
	public void beforeSuite() {
		if(!getTestEnvironment().getBrowser().isReopenForEachTest())		
			startBrowser();
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws TestEnvironmentConfigException
	 */
	private void startBrowser() {
		_logger.log(Level.FINER, "Loading Configuration...");
		browser = initBrowser();
		TestEnvironment testEnvironmentConfig = getTestEnvironment();
		_logger.finer("Opening the browser --> "+testEnvironmentConfig.getBrowser().getType());
		openBrowser();
		String actualUrl = getUrl();
		_logger.log(Level.INFO, "Loading initial page: "+actualUrl);
		browser.navigateTo(getUrl(), true);
		_logger.log(Level.INFO, "Initial page loaded successfully!! ["+actualUrl+"]");
		addJQuery(browser);
		new StorageSahiLoginLogoutTasks(browser).loginIfLoggedOut(testEnvironmentConfig.getRhscAdminCredentials());
	}

	/**
	 * 
	 */
	private void openBrowser() {
		try{
			browser.open();
		}catch(Exception e){
			throw new RuntimeException("could not open browser; is sahi started?", e);
		}
	}

	/**
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws TestEnvironmentConfigException
	 */
	private TestEnvironment getTestEnvironment()  {
		
		TestEnvironment te = null;
		try {
			te = TestEnvironmentConfig.getTestEnvironemt();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("file not found", e);
		} catch (IOException e) {
			throw new RuntimeException("could not read file", e);
		} catch (JAXBException e) {
			throw new RuntimeException("xml parse problem", e);
		} catch (TestEnvironmentConfigException e) {
			throw new RuntimeException(e);
		}
		return te;
	}

	/**
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws TestEnvironmentConfigException
	 */
	protected String getUrl() {
		String actualUrl;
		if(isHttpEnabled()){
			actualUrl = getTestEnvironment().getRhsGuiHttpUrl();
		}else{
			actualUrl = getTestEnvironment().getRhsGuiHttpsUrl();
		}
		return actualUrl;
	}

	/**
	 * @return 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws TestEnvironmentConfigException
	 */
	private StorageBrowser initBrowser() {
		 return new StorageBrowser(getTestEnvironment().getBrowser().getPath(), 
				getTestEnvironment().getBrowser().getType(), 
				getTestEnvironment().getBrowser().getOptions(), 
				getTestEnvironment().getSahi().getBaseDir(), 
				getTestEnvironment().getSahi().getUserDataDir());
	}

  public void addJQuery(Browser browser) {
    String script;
	try {
		script = FileUtil.fileToString(new File("resources/jquery.min.js"));
	} catch (IOException e) {
		throw new RuntimeException("jquery js file not found or could not be read",e);
	}
    _logger.log(Level.FINE, "adding JQuery");
    browser.execute(script);
    _logger.log(Level.INFO, "jQuery added");
  }

	/*
	 * Closing the browser
	 */
	@AfterSuite(alwaysRun=true)
	public void afterSuiteTasks() {
		if(!getTestEnvironment().getBrowser().isReopenForEachTest())		
			closeBrowser();
	}
	
	/*
	 * Closing the browser
	 */
	@AfterMethod(alwaysRun=true)
	public void afterTest() {
		//TODO not safe...
		if(ReportEngineListener.reportEngine != null && ReportEngineListener.reportEngine.isClientConfigurationSuccess()){
			ReportEngineListener.reportEngine.takeScreenShot();
		}
		if(getTestEnvironment().getBrowser().isReopenForEachTest())		
			closeBrowser();
	}

	/**
	 * 
	 */
	private void closeBrowser() {
		if(browser.isOpen())		
			_logger.log(Level.FINER, "Closing browser...");
			browser.close();
	}
	
	public Object[][] convertListTo2dArray(ArrayList<Object> list) {
		if (list.size() == 0) return new Object[0][0]; // avoid a null pointer exception
		Object[][] array = new Object[list.size()][];
		int i=0;
		for (Object item: list){
			array[i] = new Object[]{item};
			i++;
		}
		return array;
	}
	
	@Deprecated
	public static StorageBrowser getStorageSahiTasks(){
		return browser;
	}

	public static StorageBrowser getBrowser(){
		return browser;
	}

	/**
	 * @return the httpEnabled
	 */
	public static boolean isHttpEnabled() {
		return httpEnabled;
	}

	/**
	 * @param httpEnabled the httpEnabled to set
	 */
	public static void setHttpEnabled(boolean httpEnabled) {
		SahiTestBase.httpEnabled = httpEnabled;
	}
	
	public static void main(String[] args){
		TestEnvironmentConfig.getTestEnvironment();
	}

}
