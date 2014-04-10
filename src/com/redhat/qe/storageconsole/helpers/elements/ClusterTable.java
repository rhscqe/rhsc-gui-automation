/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;

/**
 * @author dustin 
 * Sep 11, 2013
 */
public class ClusterTable extends TableElement{

	/**
	 * 
	 */
	private static final JQuery SELECTOR = new JQuery("th:contains(Data Center):last").addCall("closest","table");

	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public ClusterTable(Browser browser) {
		super(SELECTOR, browser);
	}

}
