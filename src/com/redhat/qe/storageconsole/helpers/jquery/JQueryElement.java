/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.jquery;

import com.redhat.qe.storageconsole.helpers.WaitUtil;

import dstywho.timeout.Timeout;
import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;


public class JQueryElement {
	private JQuery jqueryObj;
	private Browser browser;
	
	public JQueryElement(String selector, Browser browser ) {
		this(new JQuery(selector),browser);
	}

	public JQueryElement(JQuery jqueryObj, Browser  browser){
		this.browser = browser;
		this.jqueryObj = jqueryObj;
	}
	
	public JQuery getJqueryObject(){
		return this.jqueryObj;
	}
	
	public ElementStub getElementStub() {
		return getBrowser().accessor(getJqueryObject().addCall("get", new JsGeneric("0")).toString());
	}
	
	public Browser getBrowser() {
		return this.browser;
	}
	
	public String getText(){
		return getJqueryObject().addCall("text").fetch(getBrowser());
	}
	
	public boolean waitUntilVisible(Timeout timeout){
		return WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(getElementStub()), timeout.toSeconds(),  getClass().getSimpleName() +" element is visible");
	}

	public boolean waitUntilVisible(){
		return waitUntilVisible(Timeout.TIMEOUT_TEN_SECONDS);
	}
	public boolean waitUntilNotVisible(){
		return WaitUtil.waitUntil(new WaitUtil.ElementIsNotVisible(getElementStub()), 10,  getClass().getSimpleName() +" element is visible");
	}
	
	public JQueryElement createElement(JQuery selector){
		return new JQueryElement(selector,getBrowser());
	}

}
