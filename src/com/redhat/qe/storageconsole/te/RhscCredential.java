/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author dustin 
 * May 9, 2013
 */
public class RhscCredential {
	private String username;
	private String password;
	private String domain;

	@XmlAttribute(name="username")
	public void setUsername(String username) {
		this.username = username;
	}
	
	@XmlAttribute(name="password")
	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlAttribute(name="domain")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return this.domain;
	}
	
}
