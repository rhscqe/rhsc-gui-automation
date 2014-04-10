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
public class FooterPanel extends JQueryElement{

	public static JQuery SELECTOR =new JQuery("td:contains(Last Message:)").addCall("closest","table").addCall("first")
			.addCall("parent").addCall("parent");
	/**
	 * @param browser
	 */
	public FooterPanel(StorageBrowser browser) {
		super(SELECTOR, browser);
	}

	

}
