/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.pages.components;

import java.util.ArrayList;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

import com.google.gson.Gson;
import com.redhat.qe.storageconsole.helpers.PagePanels;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;

/**
 * @author dustin 
 * Jul 16, 2013
 */
public class LHSAccordion extends JQueryElement{
	
	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public LHSAccordion(JQuery jqueryObj, Browser browser) {
		super(jqueryObj, browser);
	}
	
	public LHSAccordion(Browser browser) {
		super(new JQuery("." + PagePanels.GWT_STACK_LAYOUT_PANEL), browser);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getSectionNames(){
		String json = getJqueryObject().find(".gwt-StackLayoutPanelHeader")
			.addCall("map", "function(){ return jQuery(this).text();}")
			.addCall("get").fetchToJson(getBrowser());
		return new Gson().fromJson(json, ArrayList.class);
	}
	
	public JQuery getHeaderByName(String name){
		return getJqueryObject().find(String.format(".gwt-StackLayoutPanelHeader:contains(%s)", name));
	}
	
	public JQuery getSection(String headerName){
		return getHeaderByName(headerName).addCall("parent", "div").addCall("prev", "div");
	}	
	
	

}
