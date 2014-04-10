/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jQueryPagePanels;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;

/**
 * @author dustin 
 * Mar 21, 2014
 */
public class MainTabRefreshButton extends JQueryElement{

	private static final JQuery SELECTOR = new jQueryPagePanels().getMainTabPanel().find("div[id$=refreshButton]");

	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public MainTabRefreshButton(Browser browser) {
		super(SELECTOR, browser);
	}
	
	public void click(){
		getElementStub().click();
	}
	
}
