/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;

/**
 * @author dustin 
 * Mar 21, 2014
 */
public class InputField extends JQueryElement{

	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public InputField(JQuery jqueryObj, Browser browser) {
		super(jqueryObj, browser);
	}
	
	public String getValue(){
		return getJqueryObject().addCall("value").fetch(getBrowser());
		
	}


}
