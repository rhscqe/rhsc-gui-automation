/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.pages.components;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;

/**
 * @author dustin 
 * Oct 3, 2013
 */
public class FooterEventTable extends TableElement{
	
	public static JQuery SELECTOR = FooterPanel.SELECTOR.find("table:last");

	public FooterEventTable(Browser browser) {
		super(SELECTOR, browser);
	}
	

}
