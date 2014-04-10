/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 2, 2012
 */
public class Sahi {
	
	private String baseDir = null;
	private String userDataDir = null;

	/**
	 * @return the baseDir
	 */
	public String getBaseDir() {
		return baseDir;
	}

	/**
	 * @param baseDir the baseDir to set
	 */
	@XmlAttribute(name="baseDir")
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	/**
	 * @return the userDataDir
	 */
	public String getUserDataDir() {
		return userDataDir;
	}

	/**
	 * @param userDataDir the userDataDir to set
	 */
	@XmlAttribute(name="userDataDir")
	public void setUserDataDir(String userDataDir) {
		this.userDataDir = userDataDir;
	}
}
