/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;
import com.redhat.qe.storageconsole.helpers.pages.components.SearchPanel;
import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;

/**
 * @author dustin 
 * Mar 13, 2013
 */
public class jQueryPagePanels{
	/**
	 * 
	 */
	public  static final String GWT_STACK_LAYOUT_PANEL = "gwt-StackLayoutPanel";
	
	
	public JQuery getRHSView(){
		return new JQuery(".gwt-SplitLayoutPanel .gwt-SplitLayoutPanel .gwt-SplitLayoutPanel");
	}

	//generally where hte main table is for the main tab
	public JQuery getMainTabPanel(){
		return getRHSView().find("> div:last");
	}

	public JQuery getMainTabSubPanel(){
		return getRHSView().find("> div:eq(1)");
	}
	
	public static JQuery getSearchPanelSelector(){
		return new JQuery("div:has( > span:contains('Search'))");
		
	}


	
}
