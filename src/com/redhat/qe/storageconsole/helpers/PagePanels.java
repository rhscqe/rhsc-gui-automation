/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

/**
 * @author dustin 
 * Mar 13, 2013
 */
public class PagePanels{
	/**
	 * 
	 */
	public  static final String GWT_STACK_LAYOUT_PANEL = "gwt-StackLayoutPanel";
	Browser browser;
	public PagePanels(Browser browser){
		this.browser = browser;
	}
	
	//Tabs at the top
	public  ElementStub getMainTabs(){
		return getSearchPanel().parentNode();
	}

	public  ElementStub getSearchPanel(){
		return browser.div("/Search:/").parentNode();
	}
	
	public ElementStub getRHSView(){
		return browser.div(GWT_STACK_LAYOUT_PANEL).in(browser.div(GWT_STACK_LAYOUT_PANEL).in(browser.div(GWT_STACK_LAYOUT_PANEL)));
	}
	public ElementStub getLHSAccordion(){
		return browser.div(GWT_STACK_LAYOUT_PANEL);
	}
	

	
}
