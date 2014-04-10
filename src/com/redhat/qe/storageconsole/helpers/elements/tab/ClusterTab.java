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
public class ClusterTab extends Tab  {
	
	private static final String CLUSTER_TABLE_REFERENCE = "table:has(th:contains(Compatibility Version)):eq(1)";
	
	public ClusterTab(Browser browser){
		super(browser);
	}
	
	public TableElement getTable(){
		return new TableElement(CLUSTER_TABLE_REFERENCE, browser);
	}
	
	public boolean waitUntilArrived(){
		return getTable().waitUntilVisible();
	}


}
