/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements.tab;

import com.redhat.qe.storageconsole.helpers.elements.TableElement;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;


/**
 * @author dustin 
 * May 21, 2013
 */
public class VolumeTab extends Tab  {
	
	private static final String TABLE_REFERENCE = "table:has(th:contains(Transport Types)):eq(1)";
	
	public VolumeTab(Browser browser){
		super(browser);
	}
	
	public TableElement getTable(){
		return new TableElement(TABLE_REFERENCE, browser);
	}
	
	public boolean waitUntilArrived(){
		return getTable().waitUntilVisible();
	}


}
