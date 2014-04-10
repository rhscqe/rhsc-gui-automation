/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.pages.components;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;


/**
 * @author dustin 
 * Feb 22, 2013
 */
public class TreeComponent extends PageComponent {

	/**
	 * @param browser
	 */
	public TreeComponent(StorageBrowser browser) {
		super(browser);
	}
	
	public ElementStub getExpandAllButton(){
		return getBrowser().div("Expand All");
	}

	public TreeNode getNode(String nodeName){
		return TreeNode.getNodeByName(getBrowser(), nodeName);
	}
	

}
