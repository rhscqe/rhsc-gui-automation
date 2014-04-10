/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.helpers.jquery.JsCallChain;
import com.redhat.qe.storageconsole.helpers.jquery.JsGeneric;
import com.redhat.qe.storageconsole.helpers.jquery.JsString;
import com.redhat.qe.storageconsole.helpers.jquery.SurroundString;

import net.sf.sahi.client.Browser;

public class Row extends JQueryElement {

	
	public Row(JQuery jqueryObj, Browser browser) {
		super(jqueryObj, browser);
	}

	public Row(String selector, Browser browser) {
		super(selector, browser);
	}

	public Cell getCell(int index){
		return new Cell(getJqueryObject().addCall("find", new JsString("> td:eq(%s)", index)), getBrowser());
	}
	
	public int getCellIndexWithText(String text){
		return getData().indexOf(text);
	}

	public boolean isContainsCellText(String... texts){
		for(String text : texts){
			if(!getData().contains(text))
				return false;
		}
		return true;
	}

	
	public ArrayList<String> getData(){
		Type listOfString  = new TypeToken<ArrayList<String>>(){}.getType();
		String jsonData = getCellsJqueryObject()
				.addCall("map",new JsGeneric("function(){ return jQuery(this).text(); }"))
				.addCall("get").fetchToJson(getBrowser());
		return new Gson().fromJson(jsonData, listOfString);
	}

	/**
	 * @return
	 */
	private JQuery getCellsJqueryObject() {
		return getJqueryObject().addCall("find", new JsString("> td"));
	}
	
	private int getNumCells(){
		return Integer.parseInt(getCellsJqueryObject().addCall("size").fetch(getBrowser()));
	}



}
