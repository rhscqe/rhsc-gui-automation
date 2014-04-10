/**
 *
 */
package com.redhat.qe.storageconsole.te;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author shruti
 * Jun 10, 2013
 */
public class ClusterCompatibilityVersion {

	private String major= null;
	private String minor= null;
	
	
	public String getMinor() {
		return this.minor;
	}

	@XmlAttribute(name="minor")
	public void setMinor(String minor) {
		this.minor = minor;
	}

	public String getMajor() {
		return this.major;
	}
	
	
	@XmlAttribute(name="major")
	public void setMajor(String major) {
		this.major= major;
	}
	
	public String toString(){
		return String.format("%s.%s",getMajor(),getMinor());
	}
}
