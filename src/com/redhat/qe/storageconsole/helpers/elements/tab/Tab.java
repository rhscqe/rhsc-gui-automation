/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements.tab;

import net.sf.sahi.client.Browser;

/**
 * @author dustin 
 * May 21, 2013
 */
public abstract class Tab {

	protected Browser browser;
	public Tab(Browser browser){
		this.browser = browser;
	}
	/**
	 * 
	 */
	public Tab() {
		super();
	}
	
	
	public abstract boolean waitUntilArrived() ;

}