/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.jquery;

/**
 * @author dustin 
 * Dec 20, 2012
 */
public class SurroundString {
	public static String byDoubleQuotes(String string){
		return String.format("\"%s\"", string);
	}

}
