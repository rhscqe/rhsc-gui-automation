/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.pages.components;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;

/**
 * @author dustin 
 * Aug 26, 2013
 */
public class MainTabPanel extends JQueryElement{

	public static JQuery SELECTOR = RHSPanel.SELECTOR.find("> div:has( >div >div > div  ):last");
	/**
	 * @param browser
	 */
	public MainTabPanel(StorageBrowser browser) {
		super(SELECTOR, browser);
	}
	
	/*
	 * Finds available Refresh button icon and clicks
	 */
	
	public void clickRefresh(){
		JQueryElement elem = new JQueryElement(new JQuery("[id$=refreshPanel_refreshButton]"), getBrowser());
		if(elem.getElementStub().exists()){
			elem.getElementStub().click();
		}
	}


	

}
