/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.pages.components;

import java.util.ArrayList;
import java.util.List;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;

/**
 * @author dustin 
 * Aug 23, 2013
 */
public class SearchSuggestionsPopup extends JQueryElement{
	
	public final static JQuery SELECTOR = new JQuery(".suggestPopupMiddle:visible:first");

	public SearchSuggestionsPopup(Browser browser) {
		super(SELECTOR, browser);
	}
	
	public int getSuggestionCount(){
		String raw = getSuggestionMatch().addCall("size").fetch(getBrowser());
		return Integer.parseInt(raw);
	}
	
	public List<JQueryElement> getSuggestions(){
		ArrayList<JQueryElement> list = new ArrayList<JQueryElement>();
		for(int i=0; i< getSuggestionCount() ;i ++ ){
			JQueryElement elem = new JQueryElement(getSuggestionMatch().find(String.format(":eq(%s)", i)), getBrowser());
			list.add(elem);
		}
		return list;
	}

	/**
	 * @return
	 */
	private JQuery getSuggestionMatch() {
		return getJqueryObject().find("tr");
	}

}
