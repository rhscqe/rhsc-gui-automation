/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.jquery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import net.sf.sahi.client.Browser;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class JsCallChain {
	private static Logger LOG = Logger.getLogger(JQuery.class.getName());
	
	private final List<JsCallable> calls;
	
	/**
	 * @param calls
	 */
	public JsCallChain() {
		this.calls = new ArrayList<JsCallable>();
	}
	
	protected JsCallChain(List<JsCallable> calls) {
		this.calls = new ArrayList<JsCallable>(calls);
	}
	
	public JsCallChain addCall(String methodName, JsArgument... args) {
		return addCall( new JsCall(methodName, args));
	}
	
	public JsCallChain addCall(JsCallable callable) {
		ArrayList<JsCallable> clonedCalls = new ArrayList<JsCallable>(calls);
		clonedCalls.add(callable);
		return new JsCallChain(clonedCalls);
	}
	
	public JsCallChain property(String property){
		return addCall(new JsProperty(property));
	}

	public String toString(){
		Collection<String> strngifiedCallables = Collections2.transform(calls, new Function<JsCallable, String>() {
			 public String apply(JsCallable from){ return from.toString(); }
		});
		return StringUtils.join(strngifiedCallables, ".");
	}

	public String fetch(Browser browser) {
		LOG.fine("javascript call:" + toString());
		return browser.fetch(toString());
	}
	
	public String fetchToJson(Browser browser){
		LOG.fine("javascript call:" + new JsCall("JSON.stringify", new JsGeneric(toString())).toString());
		return browser.fetch(new JsCall("JSON.stringify", new JsGeneric(toString())).toString());	
	}
	
	/**
	 * @return
	 */
	public List<JsCallable> getCallStack() {
		return calls;
	}

		
}
