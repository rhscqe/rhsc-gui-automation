/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.jquery;

/**
 * @author dustin 
 * Dec 19, 2012
 */
public class JsProperty implements JsCallable{
	private String property;

	public JsProperty(String property){
	  this.property = property;	
	}
	
	public String toString(){
		return property;
	}

}
