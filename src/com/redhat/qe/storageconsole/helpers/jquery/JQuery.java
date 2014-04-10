package com.redhat.qe.storageconsole.helpers.jquery;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

public class JQuery {
	
	public static JQuery toJQuery(JsCallChain jsObj){
		return toJQuery(new JsGeneric(jsObj.toString()));
	}
	
	public static JQuery toJQuery(JsArgument arg ){
		JsCallChain wrap = new JsCallChain().addCall("jQuery", arg);
		return  new JQuery(wrap );
	}

	public static JQuery toJQuery(ElementStub arg ){
		return  toJQuery(new JsGeneric(arg.toString()));
	}
	
	private JsCallChain jsObj;
	
	public JQuery(String selector){
		this.jsObj  = new JsCallChain().addCall("jQuery", new JsString(selector));
	}
	
	private JQuery(JsCallChain jsObj){
		this.jsObj = jsObj;
	}

	public JsCallChain toDomObject(){
		return jsObj.addCall("get", new JsGeneric("0"));
	}
	
	public ElementStub toElementStub(Browser browser){
		return browser.accessor(toDomObject().toString());
	}
	
	public JQuery addCall(String methodName, JsArgument... args) {
		return new JQuery(jsObj.addCall(methodName, args));
	}
	
	public JQuery addCall(String methodName, String args) {
		return addCall(methodName, new JsString(args));
	}

	public String fetchToJson(Browser browser) {
		return jsObj.fetchToJson(browser);
	}
	
	public String fetch(Browser browser) {
		return jsObj.fetch(browser);
	}

	/**
	 * @param string
	 * @return 
	 * @return
	 */
	public JsCallChain property(String string) {
		return jsObj.property(string);
	}

	/**
	 * @return 
	 * @return
	 */
	public JsCallChain getCallStack() {
		return jsObj;
	}
	
	public String toString(){
		return jsObj.toString();
	}
	
	public JQuery find(String selector){
		return addCall("find", new JsString(selector));
	}
	
}
