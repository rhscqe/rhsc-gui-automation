/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.redhat.qe.factories.HostFactory;
import com.redhat.qe.model.Host;
import com.redhat.qe.storageconsole.mappper.ClusterMap;
import com.redhat.qe.storageconsole.mappper.ServerMap;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 2, 2012
 */
public class Server implements Sshable,IServer{
	private String description = null;
	private String name = null;	
	private String hostname = null;	
	private String login = null;	
	private String password = null;	
	private List<Bricks> bricks = new ArrayList<Bricks>();
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@XmlAttribute(name="name")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
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
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	@Override
	@XmlAttribute(name="hostname")
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	@Override
	@XmlAttribute(name="login")
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	@Override
	@XmlAttribute(name="password")
	public void setPassword(String password) {
		this.password = password;
	}
	

	/**
	 * @return the bricks
	 */
	public List<Bricks> getBricks() {
		return this.bricks;
	}

	/**
	 * @param bricks the bricks to set
	 */
	@XmlElement(name="bricks")
	public void setBricks(List<Bricks> bricks) {
		this.bricks = bricks;
	}
	
	public List<Brick> getBricks(String name) throws TestEnvironmentConfigException{
		for(Bricks brickBase  : this.bricks){
			if(brickBase.getName().equals(name)){
				return brickBase.getBricks();
			}
		}
		throw new TestEnvironmentConfigException("Selected Brick Setup ["+name+"] is not available on the list!");
	}
	
	public Host toHost(){
		return HostFactory.create(getName(), getHostname(), getPassword(), null);
	}
	
	public ServerMap getServerMap(ClusterMap cluster){
		ServerMap server = new ServerMap();
		server.setServerHostIP(getHostname());
		server.setServerName(getName());
		server.setServerUsername(getLogin());
		server.setServerPassword(getPassword());
		server.setResourceLocation(ServerMap.DEFAULT_RESOURCE_LOCATION);
		server.setClusterName(cluster.getClusterName());
		server.setClusterDescription(cluster.getClusterDescription());
		server.setClusterCompatibilityVersion(cluster.getClusterCompatibilityVersion());
		return server;
	}

}
