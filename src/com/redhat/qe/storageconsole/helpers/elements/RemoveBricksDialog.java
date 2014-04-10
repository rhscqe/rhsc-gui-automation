/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;

import net.sf.sahi.client.Browser;
/**
 * @author dustin 
 * Dec 3, 2013
 */
public class RemoveBricksDialog extends Dialog{

	public RemoveBricksDialog(Browser browser){
		super("Remove Bricks", browser);
	}
	
	public JQueryElement getCheckbox(){
		return new JQueryElement(getJqueryObject().find("input:checkbox"), getBrowser());
	}
	
}
