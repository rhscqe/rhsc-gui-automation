/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements.volume;

import net.sf.sahi.client.Browser;

import com.google.common.base.Joiner;
import com.redhat.qe.storageconsole.helpers.elements.Dialog;
import com.redhat.qe.storageconsole.helpers.elements.InputField;
import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;

/**
 * @author dustin 
 * Mar 21, 2014
 */
public class RebalanceStatusDialog extends Dialog{

	/**
	 * 
	 */
	private static final String TITLE = "Rebalance Status";

	/**
	 * @param title
	 * @param browser
	 */
	public RebalanceStatusDialog(Browser browser) {
		super(TITLE, browser);
	}
	
	public RebalanceStatusDialogTable getTable(){
		return new RebalanceStatusDialogTable(new JQuery("table:has( > thead:contains(Files Scanned))"), getBrowser());
	}
	
	
	public JQuery getLabel(String name){
		return getJqueryObject().find(Joiner.on(" ").join(new String[]{"label:contains(",name,"):first" }));
	}
	
	public InputField getField(JQuery label){
		return new InputField(label.find("~").find("input:first"), getBrowser());
	}
	

}
