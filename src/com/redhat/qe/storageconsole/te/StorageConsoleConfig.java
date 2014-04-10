/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 2, 2012
 */
@XmlRootElement(name = "config")
public class StorageConsoleConfig {
	Logger _logger = Logger.getLogger(StorageConsoleConfig.class.getName());
		
    private float versionID;
    private List<TestEnvironment> testEnvironments = new ArrayList<TestEnvironment>();

	/**
	 * @return the versionID
	 */
	public float getVersionID() {
		return versionID;
	}

	/**
	 * @param versionID the versionID to set
	 */
	@XmlAttribute(name="version")
	public void setVersionID(float versionID) {
		this.versionID = versionID;
	}

	/**
	 * @return the testEnvironments
	 */
	public List<TestEnvironment> getTestEnvironments() {
		return testEnvironments;
	}

	/**
	 * @param testEnvironments the testEnvironments to set
	 */
	@XmlElement(name="TestEnvironment")
	public void setTestEnvironments(List<TestEnvironment> testEnvironments) {
		this.testEnvironments = testEnvironments;
	}
	
	/**
	 * 
	 */
	public StorageConsoleConfig() {
		// TODO Auto-generated constructor stub
	}
	
}
