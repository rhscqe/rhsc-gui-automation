/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements.volume;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.elements.ContextMenu;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;

/**
 * @author dustin 
 * Mar 21, 2014
 */
public class ActionMenu extends ContextMenu{
	private static String jquerySelector = ".popupContent:visible";
	/**
	 * @param browser
	 */
	public ActionMenu(Browser browser) {
		super(new JQuery(jquerySelector), browser);
	}

}
