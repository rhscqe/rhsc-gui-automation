/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.pages.components.SubtabPanel;

/**
 * @author dustin 
 * Sep 3, 2013
 */
public class SubTabBrickTable extends TableElement{
	
	public static final JQuery SELECTOR = SubtabPanel.SELECTOR.find("th:contains(Brick Directory):eq(1)").addCall("closest", "table");

	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public SubTabBrickTable(Browser browser) {
		super(SELECTOR, browser);
	}

}
