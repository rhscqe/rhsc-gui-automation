/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;

/**
 * @author dustin 
 * Aug 29, 2013
 */
public class BrickCreationTable extends TableElement {

	private static final JQuery SELECTOR = new JQuery("th:contains(Brick Directory)").addCall("closest","table");
	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public BrickCreationTable(Browser browser) {
		super(SELECTOR, browser);
	}
	
	public void checkRow(int index){
		new JQueryElement(getRow(index).getCell(0).getJqueryObject().find("input"),getBrowser()).getElementStub().check();
	}
	
	@Override
	public int getRowCount(){
		return super.getRowCount() -1;
	}

}
