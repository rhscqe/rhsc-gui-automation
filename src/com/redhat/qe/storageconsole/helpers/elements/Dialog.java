package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.helpers.jquery.JsGeneric;

public class Dialog extends JQueryElement{
	
	/**
	 * 
	 */
	private static final String select_text_is_red = "function(){ return jQuery(this).css('color') == 'rgb(255, 0, 0)'; }";


	public static String getJqueryLocator(String name){
		return String.format(".gwt-DialogBox:contains('%s')", name);
	}

	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public Dialog(String title, Browser browser) {
		super(new JQuery(getJqueryLocator(title)), browser);
	}
	
	public JQueryElement getField(String labelName){
		JQuery elem = getJqueryObject().find(String.format("div:has( > label:contains(%s)) input", labelName));
		return new JQueryElement(elem, getBrowser());
	}
	
	public JQueryElement getOkButton(){
		JQuery selector = getJqueryObject().find("div.html-face:contains(OK)").addCall("parent");
		return new JQueryElement(selector, getBrowser());
	}
	public JQueryElement getCancelButton(){
		JQuery cancel = getJqueryObject().find("div[title=Cancel]");
		return new JQueryElement(cancel, getBrowser());
	}
	
	
	public JQuery getValidationMessages(){
		return getJqueryObject().find("div").addCall("filter", new JsGeneric(select_text_is_red)).addCall("text");
	}

	public boolean hasValidationMessages(){
		return getValidationMessages().fetch(getBrowser()).length() > 0;
	}
	

}
