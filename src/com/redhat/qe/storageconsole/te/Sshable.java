/**
 * 
 */
package com.redhat.qe.storageconsole.te;

/**
 * @author dustin 
 * Aug 27, 2013
 */
public interface Sshable {


	/**
	 * @return the hostname
	 */
	public abstract String getHostname();

	/**
	 * @return the login
	 */
	public abstract String getLogin();

	public abstract void setPassword(String password);

	public abstract void setLogin(String login);

	public abstract void setHostname(String hostname);

	/**
	 * @return the password
	 */
	public abstract String getPassword();
	
	

}