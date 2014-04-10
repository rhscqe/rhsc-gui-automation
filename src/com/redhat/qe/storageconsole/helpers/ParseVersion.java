/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

/**
 * @author dustin 
 * Oct 10, 2013
 */
public class ParseVersion {
	public int getMajorVersion(String version){
		return Integer.parseInt(new RegexMatch(version).find("^\\d+").get(0).getText());
		
	}
	public int getMinorVersion(String version){
		return Integer.parseInt(new RegexMatch(version).find("\\d+$").get(0).getText());
	}
}
