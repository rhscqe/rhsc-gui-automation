/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.pages.components;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jQueryPagePanels;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;

/**
 * @author dustin 
 * Aug 23, 2013
 */
public class SearchPanel extends JQueryElement{


	/**
	 * 
	 */
	private static final JQuery SELECTOR = jQueryPagePanels.getSearchPanelSelector();

	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public SearchPanel(Browser browser) {
		super(SELECTOR, browser);
	}
	
	public JQueryElement getSearchBar(){
		return createElement(getJqueryObject().find("input"));
	}
	
	public void search(String query){
		getSearchBar().getElementStub().setValue(query);
		pressEnter();
	}

	/**
	 * 
	 */
	private void pressEnter() {
		getSearchBar().getElementStub().keyDown(13, 13);
	}
	
	public SearchSuggestionsPopup getSearchSuggestions(){
		return new SearchSuggestionsPopup(getBrowser());
	}

	


}
