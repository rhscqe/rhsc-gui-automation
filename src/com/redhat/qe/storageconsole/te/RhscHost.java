/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author dustin 
 * Jan 24, 2013
 */
public class RhscHost implements Sshable {

	private String login;
	private String password;
	private String hostname;
	
	/* (non-Javadoc)
	 * @see com.redhat.qe.storageconsole.te.Sshable#getHostname()
	 */
	@Override
	public String getHostname() {
		return this.hostname;
	}
	/**
	 * @param hostname the hostname to set
	 */
	@XmlAttribute(name="hostname")
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	/* (non-Javadoc)
	 * @see com.redhat.qe.storageconsole.te.Sshable#getLogin()
	 */
	@Override
	public String getLogin() {
		return this.login;
	}
	/**
	 * @param login the login to set
	 */
	@XmlAttribute(name="login")
	public void setLogin(String login) {
		this.login = login;
	}
	/* (non-Javadoc)
	 * @see com.redhat.qe.storageconsole.te.Sshable#getPassword()
	 */
	@Override
	public String getPassword() {
		return this.password;
	}
	/**
	 * @param password the password to set
	 */
	@XmlAttribute(name="password")
	public void setPassword(String password) {
		this.password = password;
	}
}
