/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;

/**
 * @author dustin 
 * Aug 8, 2013
 */
public class ContextMenu extends JQueryElement{
	private static String jquerySelector = ".gwt-PopupPanel:visible";

	public ContextMenu(Browser browser) {
		super(new JQuery(jquerySelector), browser);
	}


	public ContextMenu(JQuery jqueryObj, Browser browser) {
		super(jqueryObj, browser);
	}
	
	
	public JQuery getItem(String name){
		return getJqueryObject().find(String.format("tr:contains(%s) td", name));
	}

}
