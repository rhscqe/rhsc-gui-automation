package com.redhat.qe.storageconsole.helpers.pages.components;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;

/**
 * 
 */

/**
 * @author dustin Aug 26, 2013
 */
public class SubtabPanel extends JQueryElement{

	public final static JQuery SELECTOR = MainTabPanel.SELECTOR.addCall("prev")
			.addCall("prev");

	public SubtabPanel(StorageBrowser browser) {
		super(SELECTOR, browser);
	}
	
	public JQueryElement getTabSection(){
		return new JQueryElement(getJqueryObject().find("> div > div"), getBrowser());
	}
	
	public void clickOnTab(String name){
		getBrowser().div(name).in(getTabSection().getElementStub()).click();
		
	}


}
