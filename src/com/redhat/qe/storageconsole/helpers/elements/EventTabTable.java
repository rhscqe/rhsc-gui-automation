/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.pages.components.MainTabPanel;

/**
 * @author dustin 
 * Aug 29, 2013
 */
public class EventTabTable extends TableElement{
	
	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public EventTabTable(Browser browser) {
		super(SELECTOR, browser);
	}

	public static final JQuery SELECTOR =  MainTabPanel.SELECTOR.find("th:contains(Time)").addCall("closest", "table");
	

}
