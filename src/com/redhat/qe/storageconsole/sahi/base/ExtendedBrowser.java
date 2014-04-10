/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.base;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;
import net.sf.sahi.config.Configuration;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 5, 2012
 */
public class ExtendedBrowser extends Browser{
	public Logger _logger = Logger.getLogger(ExtendedBrowser.class.getName());

	public ExtendedBrowser(String browserName, String basePath, String userDataDirectory){
		super(browserName);
		initSahi(basePath, userDataDirectory);
	}

	public ExtendedBrowser(String browserPath, String browserType, String browserOption, String basePath, String userDataDirectory){
		super(browserPath, browserType, browserOption);
		initSahi(basePath, userDataDirectory);
	}

	private void initSahi(String basePath, String userDataDirectory){
		Configuration.initJava(basePath, userDataDirectory);
		_logger.log(Level.FINE, "Sahi configuration init process done...");
	}

	/*
	 * Waiting for the element on the browser.
	 */
	public boolean waitForElementExists(Browser browser, ElementStub elementStub, String element, long maximunWaitTime){
		_logger.info("Waiting for the element: ["+element+"], Remaining wait time: "+(maximunWaitTime/1000)+" Second(s)...");
		while(maximunWaitTime >=  0){
			if(elementStub.exists()){
				_logger.info("Element ["+element+"] exists.");
				return true;
			}else{
				browser.waitFor(500);
				maximunWaitTime -= 500;
				if((maximunWaitTime%(1000*5)) <= 0){
					_logger.info("Waiting for the element: ["+element+"], Remaining wait time: "+(maximunWaitTime/1000)+" Second(s)...");
				}
			}
		}		
		_logger.warning("Failed to get the element! ["+element+"]");
		return false;
	}

	/*
	 * Uses to convert String value to Hash Mapping (Key Value pair)
	 * Example: {key1=value1,value2,value3}{key2=valuse11,value12,value13}{key3=valuer21,value22,value23,value24} to HashMap<String, String>
	 */
	public HashMap<String, String> getListMapFromString(String listMapString){
		HashMap<String, String> keyValueMap = new HashMap<String, String>(); 
		String regexStr = "[\\{.*?\\}]";
		String[] contents = listMapString.split(regexStr);
		for(String content : contents){
			if(content.trim().length()>0){
				String[] keyValue = content.split("=");
				keyValueMap.put(keyValue[0].trim(), keyValue[1].trim());			
			}			
		}
		return keyValueMap;
	}

}
