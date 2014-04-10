/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

public class SahiElement {

	private Browser browser;
	private ElementStub element;

	
	public SahiElement(Browser browser, ElementStub element) {
		this.browser = browser;
		this.element = element;
	}
	
	public ElementStub getElement() {
		return this.element;
	}
	
	public Browser getBrowser() {
		return this.browser;
	}
	
	public JQuery toJQueryElement() {
		return JQuery.toJQuery(getElement());
	}
	

}
