/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.*;

import org.dom4j.tree.DefaultProcessingInstruction;

import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 2, 2012
 * TODO just make the two properties from te.properties and make them environment variables
 */
public class TestEnvironmentConfig {
	private static Logger _logger = Logger.getLogger(TestEnvironmentConfig.class.getName());
	private static StorageConsoleConfig config;
	private static TestEnvironment testEnvironament;
	
    static final String TEST_ENVIRONMENT_NAME        = "TEST_ENVIRONMENT_NAME";
    static final String TEST_ENVIRONMENT_XML_FILE    = "TEST_ENVIRONMENT_XML_FILE";

    static final String HTTP_ENABLED                         = "HTTP_ENABLED";
    static final String DEFAULT_PROPERTIES_LOCATION          = "resources/te/te.properties";
    static final String TEST_ENVIRONMENT_ENV_VAR = "TEST_ENVIRONMENT_FILE";
	
	public static TestEnvironment getTestEnvironemt() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		if(testEnvironament == null){
			synchronized(TestEnvironmentConfig.class){
				initTestEnvironment();
			}			
		}
		return testEnvironament;
	}
	
	public static TestEnvironment getTestEnvironment(){ 
		try {
			return getTestEnvironemt();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} catch (TestEnvironmentConfigException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static String getEnvironmentVariableFile() {
		String result = System.getenv(TEST_ENVIRONMENT_ENV_VAR);
		if (result == null) {
			return DEFAULT_PROPERTIES_LOCATION;
		} else {
			return result;
		}
	}
	
	public static Properties getTestEnvProperties() throws FileNotFoundException, IOException{
		Properties result = new Properties();
		String environmentVariableFile = getEnvironmentVariableFile();
		_logger.log(Level.INFO, "Test Environment Properties File: " + environmentVariableFile);
		result.load(new FileInputStream(environmentVariableFile));
		return result;
	}
	
	private static void initTestEnvironment() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
        String xmlFile = null;
        String testEnvironmentName = null;
        String httpEnabeld = System.getenv(HTTP_ENABLED);
        
        Properties testEnvProperties = getTestEnvProperties();
                
        xmlFile = testEnvProperties.getProperty(TEST_ENVIRONMENT_XML_FILE).trim();
        testEnvironmentName = testEnvProperties.getProperty(TEST_ENVIRONMENT_NAME).trim();
                
        httpEnabeld = testEnvProperties.getProperty(HTTP_ENABLED).trim();
			
		if((httpEnabeld != null) && httpEnabeld.equalsIgnoreCase("true")){
				SahiTestBase.setHttpEnabled(true);
		}
		// setup object mapper using the StorageConsoleConfig class
		JAXBContext context = JAXBContext.newInstance(StorageConsoleConfig.class);
		_logger.log(Level.INFO, "XML-File Location: "+xmlFile+", Test Environement Name: "+testEnvironmentName);
		// parse the XML and return an instance of the StorageConsoleConfig class
		config = (StorageConsoleConfig) context.createUnmarshaller().unmarshal(new File(xmlFile));
		_logger.log(Level.INFO, "Test Environemnt XML Configuration Version: "+config.getVersionID());
		for(TestEnvironment tmpTestEnvironment : config.getTestEnvironments()){
			if(tmpTestEnvironment.getName().equals(testEnvironmentName)){
				testEnvironament = tmpTestEnvironment;
			}
		}
		if(testEnvironament == null){
			throw new TestEnvironmentConfigException("Selected Test Environment ["+testEnvironmentName+"] not available on the selected 'xml' file!!");
		}
	}
}
