/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

import com.google.common.base.Predicate;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;

/**
 * @author dustin 
 * May 31, 2013
 */
public class GwtButton extends JQueryElement {

	/**
	 * 
	 */
	private static final int NUM_ATTEMPTS = 20;
		
		
	
	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public GwtButton(JQuery jqueryObj, Browser browser) {
		super(jqueryObj, browser);
	}

	/**
	 * @param selector
	 * @param browser
	 */
	public GwtButton(String selector, Browser browser) {
		super(selector, browser);
	}

	
	public boolean isDisabled(){
		 return getButton().addCall("hasClass", "gwt-ToggleButton-up-disabled").fetch(getBrowser()).contains("true");
	}

	private JQuery getButton() {
		return getJqueryObject().addCall("closest", ".gwt-ToggleButton");
	}
	
	public boolean waitUntilButtonEnabled(){
		return WaitUtil.waitUntil(new Predicate<Integer>(){
			public boolean apply(Integer attempt){
				return !isDisabled();
			}
		}, 10, "wait until gwt button is enabled");
		
	}


	public boolean waitUntilButtonDisabled(){
		return WaitUtil.waitUntil(new Predicate<Integer>(){
			public boolean apply(Integer attempt){
				return isDisabled();
			}
		}, NUM_ATTEMPTS, "wait until gwt button is disabled");
		
	}


}
