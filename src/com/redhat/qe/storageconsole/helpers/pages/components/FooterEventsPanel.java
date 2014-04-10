/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.pages.components;

import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;

/**
 * @author dustin 
 * Aug 26, 2013
 */
public class FooterEventsPanel extends JQueryElement{

	public static JQuery SELECTOR = FooterPanel.SELECTOR.find("table:last");
	/**
	 * @param browser
	 */
	public FooterEventsPanel(StorageBrowser browser) {
		super(SELECTOR, browser);
	}
	
	public TableElement getTable(){
		return new TableElement(SELECTOR, getBrowser());
	}

	

}
