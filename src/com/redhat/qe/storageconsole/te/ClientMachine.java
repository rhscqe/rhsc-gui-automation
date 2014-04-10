/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Sep 13, 2012
 */
public class ClientMachine {
	private String name = null;
	private String type = null;
	private String description = null;
	private String hostname = null;
	private String password = null;
	private String login=null;
	private List<String> mountPoints = new ArrayList<String>();
	private String mountPoint=null;
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @param name the name to set
	 */
	@XmlAttribute(name="name")
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}
	/**
	 * @param type the type to set
	 */
	@XmlAttribute(name="type")
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	/**
	 * @param description the description to set
	 */
	@XmlAttribute(name="description")
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the hostname
	 */
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
	/**
	 * @return the password
	 */
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
	/**
	 * @return the login
	 */
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
	/**
	 * @return the mountPoints
	 */
	public List<String> getMountPoints() {
		return this.mountPoints;
	}
	public String getMountPoint() {
		return this.mountPoint;
	}
	/**
	 * @param mountPoints the mountPoints to set
	 */
	@XmlAttribute(name="mount-points")
	public void setMountPoint(String mountPoints) {
		this.mountPoint = mountPoints;
		for(String mountPoint : mountPoints.split(",")){
			this.mountPoints.add(mountPoint.trim());
		}
	}

}
