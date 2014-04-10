/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.sahi.client.Browser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.redhat.qe.storageconsole.helpers.ListUtil;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.helpers.jquery.JsGeneric;
import com.redhat.qe.storageconsole.helpers.jquery.JsString;

public class TableElement extends JQueryElement {

	public TableElement(String selector, Browser browser) {
		super(selector, browser);
	}

	public TableElement(JQuery jqueryObj, Browser browser) {
		super(jqueryObj, browser);
	}

	public ArrayList<String> getHeaders() {
		Type listOfString = new TypeToken<ArrayList<String>>() {
		}.getType();
		String jsonData = getJqueryObject().addCall("find", new JsString("> thead > tr > th"))
				.addCall("map", new JsGeneric("function(){ return jQuery(this).text();}"))
				.addCall("get").fetchToJson(getBrowser());
		return new Gson().fromJson(jsonData, listOfString);
	}

	public Row getRow(int index) {
		return new Row(getJqueryObject().addCall("find", new JsString("> tbody > tr:eq(%s):visible", index)), getBrowser());
	}

	public ArrayList<String> getRowText(int index) {
		Type listOfString = new TypeToken<ArrayList<String>>() {
		}.getType();
		String jsonData = getJqueryObject()
				.addCall("find", new JsString("> tbody > tr:eq(%s) > td",index))
				.addCall("map", new JsGeneric("function(){ return jQuery(this).text(); }"))
				.addCall("get").fetchToJson(getBrowser());
		return new Gson().fromJson(jsonData, listOfString);
	}


	public int getRowCount() {
		String count = getJqueryObject().addCall("find", new JsString("> tbody > tr"))
				.property("length").fetch(getBrowser());
		return Integer.parseInt(count);
	}

	public List<Row> getRows() {
		ArrayList<Row> results = new ArrayList<Row>();
		int rowCount = getRowCount();
		for (int i = 0; i < rowCount; i++) {
			results.add(getRow(i));
		}
		return results;
	}
	
	public Cell findCell(String text){
		return new Cell(getJqueryObject().addCall("find",new JsString( "> tbody > tr > td:contains('%s')", text)), getBrowser());
	}

	public ArrayList<HashMap<String, String>> getData() {
		ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		ArrayList<String> headers = getHeaders();
		int rowCount = getRowCount();
		for (int i = 0; i < rowCount; i++) {
			ArrayList<String> rowText = getRowText(i);
			if (rowText.size() == headers.size()) {
				HashMap<String, String> rowData = ListUtil.joinHashMap(headers, rowText);
				results.add(rowData);
			}
		}
		return results;
	}

	public ArrayList<Row> getRowsThatContainsText(String text) {
		ArrayList<Row> result = new ArrayList<Row>();
		int rowCount = getRowCount();
		for (int i = 0; i < rowCount; i++) {
			if(getRowText(i).contains(text))
				result.add(getRow(i));
		}
		return result;
	}

	public ArrayList<Integer> getRowIndexsOfRowsThatContainsExactTexts(String... exactTexts) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		int rowCount = getRowCount();
		for (int i = 0; i < rowCount; i++) {
			if(getRow(i).isContainsCellText(exactTexts))
					result.add(i);
		}
		return result;
	}


	public Row getFirstRowThatContainsText(String text) {
		return getRow(getFirstRowIndexThatContainsText(text));
	}

	public int getFirstRowIndexThatContainsText(String text) {
		int rowCount = getRowCount();
		for (int i = 0; i < rowCount; i++) {
			if(getRowText(i).contains(text))
				return i;		
		}
		return -1;
	}

	/**
	 * @param headers
	 * @param rowText
	 * @return
	 */
	private HashMap<String, String> join(ArrayList<String> headers, ArrayList<String> rowText) {
		HashMap<String, String> rowData = new HashMap<String, String>();
		for (int j = 0; j < headers.size(); j++) {
			rowData.put(headers.get(j), rowText.get(j));
		}
		return rowData;
	}
	
	public ArrayList<Row> getRowsWithIndexLessThan(int index){
		ArrayList<Row> result = new ArrayList<Row>();
		for(int i =0; i< index; i++ ){
			Row row = new Row(getJqueryObject().find(String.format("> tbody > tr:eq(%s)", index)), getBrowser());
			result.add(row);
		}
		return result;
	}
	


}
