/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 2, 2012
 */
public class Browser {
	
	private String path= null;	
	private String type= null;	
	private String options= null;
	private boolean isRepopenForEachTest;
	private String remoteHost;
	private int remotePort;

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	@XmlAttribute(name="path")
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	@XmlAttribute(name="type")
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the options
	 */
	public String getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	@XmlAttribute(name="options")
	public void setOptions(String options) {
		this.options = options;
	}
	
	/**
	 * @return the options
	 */
	public boolean isReopenForEachTest() {
		return isRepopenForEachTest;
	}
	
	/**
	 * @param options the options to set
	 */
	@XmlAttribute(name="reopen-for-each-test")
	public void setReopenForEachTest(boolean isRepopenForEachTest) {
		this.isRepopenForEachTest = isRepopenForEachTest;
	}

	@XmlAttribute(name="remote-host")
	public void setRemoteHost(String host) {
		this.remoteHost = host;
	}

	@XmlAttribute(name="remote-port")
	public void setRemotePort(int port) {
		this.remotePort = port;
	}

	public String getRemoteHost() {
		return this.remoteHost;
	}

	public int getRemotePort() {
		return this.remotePort;
	}

}
