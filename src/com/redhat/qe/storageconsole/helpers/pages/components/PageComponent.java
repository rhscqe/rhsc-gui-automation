/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.pages.components;

import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;

/**
 * @author dustin 
 * Feb 22, 2013
 */
public class PageComponent {

	private StorageBrowser browser;

	/**
	 * @param browser2
	 */
	public PageComponent(StorageBrowser browser) {
		this.browser = browser;
	}

	/**
	 * @return the browser
	 */
	public StorageBrowser getBrowser() {
		return this.browser;
	}

}