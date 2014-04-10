/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.jquery;

/**
 * @author dustin 
 * Feb 19, 2013
 */
public class JsString implements JsArgument {
	private Object[] substitutions;
	private String template;

	public JsString(String template, Object... substitutions){
		this.template = template;
		this.substitutions = substitutions;
	}
	
	public String toString(){
		String subbed = String.format(template, substitutions);
		return String.format("\"%s\"", subbed);
	}

}
