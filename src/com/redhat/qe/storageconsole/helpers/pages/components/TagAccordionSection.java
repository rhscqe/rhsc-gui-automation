/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.pages.components;

import java.util.ArrayList;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

import com.google.gson.Gson;
import com.redhat.qe.storageconsole.helpers.PagePanels;
import com.redhat.qe.storageconsole.helpers.elements.SahiElement;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;

/**
 * @author dustin 
 * Jul 16, 2013
 */
public class TagAccordionSection {
	
	

	/**
	 * 
	 */
	private static final String SECTION_NAME = "Tags";
	private Browser browser;

	/**
	 * @return the browser
	 */
	public Browser getBrowser() {
		return this.browser;
	}

	/**
	 * @param browser the browser to set
	 */
	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	/**
	 * @param browser
	 * @param element
	 */
	public TagAccordionSection(Browser browser) {
		this.browser = browser;
	}

	public LHSAccordion getLHSAccordiaon(){
		return new LHSAccordion(getBrowser());
	}
	
	public JQueryElement getSection(){
		return new JQueryElement(getLHSAccordiaon().getSection(SECTION_NAME), getBrowser());
	}
	
}
