/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements.tab;

import com.redhat.qe.storageconsole.helpers.elements.TableElement;

import net.sf.sahi.client.Browser;


/**
 * @author dustin 
 * May 21, 2013
 */
public class ServerTab extends Tab  {
	
	private static final String TABLE_REFERENCE = "table:has(th:contains(Name)):has(th:contains(Hostname/IP)):eq(1)";
	
	public ServerTab(Browser browser){
		super(browser);
	}
	
	public TableElement getTable(){
		return new TableElement(TABLE_REFERENCE, browser);
	}
	
	public boolean waitUntilArrived(){
		return getTable().waitUntilVisible();
	}


}
