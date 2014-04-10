/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;

import net.sf.sahi.client.Browser;

/**
 * @author dustin 
 * Sep 3, 2013
 */
public class AddBrickDialog extends Dialog {

	/**
	 * 
	 */
	private static final String ADD_BRICKS = "Add Bricks";

	/**
	 * @param title
	 * @param browser
	 */
	public AddBrickDialog(Browser browser) {
		super(ADD_BRICKS, browser);
	}


}
