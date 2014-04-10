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
public class RHSPanel extends JQueryElement{

	public static JQuery SELECTOR = new JQuery(".gwt-SplitLayoutPanel .gwt-SplitLayoutPanel .gwt-SplitLayoutPanel");
	/**
	 * @param browser
	 */
	public RHSPanel(StorageBrowser browser) {
		super(SELECTOR, browser);
	}

	

}
