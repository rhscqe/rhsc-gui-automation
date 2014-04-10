/**
 * 
 */
package com.redhat.qe.storageconsole.mappper;

import com.redhat.qe.factories.ClusterFactory;
import com.redhat.qe.factories.HostFactory;
import com.redhat.qe.model.Cluster;
import com.redhat.qe.model.Host;
import com.redhat.qe.storageconsole.helpers.ParseVersion;
import com.redhat.qe.storageconsole.helpers.RegexMatch;
import com.redhat.qe.storageconsole.te.IServer;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 15, 2012
 */
//TODO FIXME composition over inheritance here
public class ServerMap extends ClusterMap {
	/**
	 * 
	 */
	public static final String DEFAULT_RESOURCE_LOCATION = "Hosts";
	private String serverName = null;
	private String serverHostIP	= null;
	private String serverUsername = null;
	private String serverPassword = null;
	private boolean serverAlreadyOnList=true;  // For Negative test cases
	
	public static ServerMap fromServer(IServer server, String cluster){
		ClusterMap clusterMap = ClusterMap.clusterMap(cluster);
		return fromServer(server, clusterMap);
	}
	
	public static ServerMap fromServer(IServer server, ClusterMap cluster){
		ServerMap serverObj = new ServerMap();
		serverObj.setResourceLocation(DEFAULT_RESOURCE_LOCATION);
		serverObj.setServerName(server.getName());
		serverObj.setServerHostIP(server.getHostname());
		serverObj.setServerPassword(server.getPassword());
		serverObj.setServerUsername(server.getLogin());
		serverObj.setClusterName(cluster.getClusterName());
		serverObj.setClusterDescription(cluster.getClusterDescription());
		serverObj.setClusterCompatibilityVersion(cluster.getClusterCompatibilityVersion());
		return serverObj;
	}
	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return this.serverName;
	}
	/**
	 * @param serverName the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	/**
	 * @return the serverIP
	 */
	public String getServerHostIP() {
		return this.serverHostIP;
	}
	/**
	 * @param serverIP the serverIP to set
	 */
	public void setServerHostIP(String serverIP) {
		this.serverHostIP = serverIP;
	}
	/**
	 * @return the serverUsername
	 */
	public String getServerUsername() {
		return this.serverUsername;
	}
	/**
	 * @param serverUsername the serverUsername to set
	 */
	public void setServerUsername(String serverUsername) {
		this.serverUsername = serverUsername;
	}
	/**
	 * @return the serverPassword
	 */
	public String getServerPassword() {
		return this.serverPassword;
	}
	/**
	 * @param serverPassword the serverPassword to set
	 */
	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}
	
	/**
	 * @return  setServerAlreadyOnList
	 */
	public Boolean getServerAlreadyOnList() {
		return this.serverAlreadyOnList;
	}
	
	/**
	 * @param setServerAlreadyOnList to set
	 */
	public void setServerAlreadyOnList(Boolean serverAlreadyOnList) {
		this.serverAlreadyOnList = serverAlreadyOnList;
	}
	
	public String toString(){
		return "[Server Map] Name:"+this.getServerName()+", Host:"+this.getServerHostIP()+", Cluster:"+this.getClusterName()+", Resoure Location:"+this.getResourceLocation();		
	}
	

	
	public Host toHost(){
		Cluster cluster = ClusterFactory.cluster(getClusterName(), getClusterDescription());
		cluster.setMajorVersion(new ParseVersion().getMajorVersion(getClusterCompatibilityVersion()));
		cluster.setMinorVersion(new ParseVersion().getMinorVersion(getClusterCompatibilityVersion()));
		return HostFactory.create(getServerName(), getServerHostIP(), getServerPassword(), cluster);
	}
}
