/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.jquery;

/**
 * @author dustin 
 * Feb 19, 2013
 */
public class JsGeneric implements JsArgument {

	private String js;

	public JsGeneric(String js){
		this.js = js;
	}
	
	public String toString(){
		return js;
	}

}
