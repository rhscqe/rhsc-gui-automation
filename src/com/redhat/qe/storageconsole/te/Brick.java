/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import javax.xml.bind.annotation.XmlValue;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 2, 2012
 */
public class Brick {

	
	private String location = null;

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	@XmlValue
	public void setLocation(String location) {
		this.location = location;
	}
}
