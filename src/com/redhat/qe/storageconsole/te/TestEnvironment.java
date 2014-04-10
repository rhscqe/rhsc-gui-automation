/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 2, 2012
 */
public class TestEnvironment {
	
	private String name;
	private String description;
	private String rhsGuiHttpUrl;
	private String rhsGuiHttpsUrl;
	private Browser browser;
	private Sahi sahi;
	private RhscHost rhscHost;
	private List<Server> servers = new ArrayList<Server>();
	private List<ClientMachine> clientMachines = new ArrayList<ClientMachine>();
	private List<GeneralKeyValueMap> generalKeyValueMaps = new ArrayList<GeneralKeyValueMap>();
	private RhscCredential  rhscAdminCredentials;
	private ClusterCompatibilityVersion clusterCompatibilityVersion = new ClusterCompatibilityVersion();

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

	@XmlElement(name="rhsc-admin-credentials")
	public void setRhscAdminCredentials(RhscCredential credentials) {
		this.rhscAdminCredentials = credentials;
	}
	
	public RhscCredential getRhscAdminCredentials (){
		return rhscAdminCredentials;
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
	 * @return the rhsGuiHttpUrl
	 */
	public String getRhsGuiHttpUrl() {
		return rhsGuiHttpUrl;
	}

	/**
	 * @param rhsGuiHttpUrl the rhsGuiHttpUrl to set
	 */
	@XmlElement(name="rhscGuiHttpUrl")
	public void setRhsGuiHttpUrl(String rhsGuiHttpUrl) {
		this.rhsGuiHttpUrl = rhsGuiHttpUrl;
	}

	/**
	 * @return the rhsGuiHttpsUrl
	 */
	public String getRhsGuiHttpsUrl() {
		return rhsGuiHttpsUrl;
	}

	/**
	 * @param rhsGuiHttpsUrl the rhsGuiHttpsUrl to set
	 */
	@XmlElement(name="rhscGuiHttpsUrl")
	public void setRhsGuiHttpsUrl(String rhsGuiHttpsUrl) {
		this.rhsGuiHttpsUrl = rhsGuiHttpsUrl;
	}

	/**
	 * @return the browser
	 */
	public Browser getBrowser() {
		return browser;
	}

	/**
	 * @param browser the browser to set
	 */
	@XmlElement(name="browser")
	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	/**
	 * @return the rhscHost
	 */
	public RhscHost getRhscHost() {
		return this.rhscHost;
	}

	/**
	 * @param rhscHost the rhscHost to set
	 */
	@XmlElement(name="rhscHost")
	public void setRhscHost(RhscHost rhscHost) {
		this.rhscHost = rhscHost;
	}

	/**
	 * @return the sahi
	 */
	public Sahi getSahi() {
		return sahi;
	}

	/**
	 * @param sahi the sahi to set
	 */
	@XmlElement(name="sahi")
	public void setSahi(Sahi sahi) {
		this.sahi = sahi;
	}

	/**
	 * @return the servers
	 */
	public List<Server> getServers() {
		return servers;
	}

	/**
	 * @param servers the servers to set
	 */
	@XmlElement(name="server")
	public void setServers(List<Server> servers) {
		this.servers = servers;
	}
	
	public Server getServer(String serverName) throws TestEnvironmentConfigException{
		for(Server server : this.servers){
			if(server.getName().equals(serverName)){
				return server;
			}
		}
		throw new TestEnvironmentConfigException("Server ["+serverName+"] is not available on the list!");
	}
	
	
	public Collection<Server> getServers(Iterable<String> names) throws  TestEnvironmentConfigException{
		List<Server> servers = new ArrayList<Server>();
		for(String server : names){
				servers.add(getServer(server.trim()));
		}
		return servers;
	}

	/**
	 * @return the clientMachines
	 */
	public List<ClientMachine> getClientMachines() {
		return this.clientMachines;
	}

	public ClientMachine getClientMachine(String clientMachineName) throws TestEnvironmentConfigException{
		for(ClientMachine clientMachine : this.clientMachines){
			if(clientMachine.getName().equals(clientMachineName)){
				return clientMachine;
			}
		}
		throw new TestEnvironmentConfigException("Client Machine ["+clientMachineName+"] is not available on the list!");
	}
	
	/**
	 * @param clientMachines the clientMachines to set
	 */
	@XmlElement(name="client-machine")
	public void setClientMachines(List<ClientMachine> clientMachines) {
		this.clientMachines = clientMachines;
	}

	/**
	 * @return the generalKeyValueMaps
	 */
	public List<GeneralKeyValueMap> getGeneralKeyValueMaps() {
		return generalKeyValueMaps;
	}

	/**
	 * @param generalKeyValueMaps the generalKeyValueMaps to set
	 */
	@XmlElement(name="general-key-value-map")
	public void setGeneralKeyValueMaps(List<GeneralKeyValueMap> generalKeyValueMaps) {
		this.generalKeyValueMaps = generalKeyValueMaps;
	}
	
	public GeneralKeyValueMap getGeneralKeyValueMapFromKey(String key) throws TestEnvironmentConfigException{
		for(GeneralKeyValueMap generalKeyValueMap : this.generalKeyValueMaps){
			if(generalKeyValueMap.getKey().equals(key)){
				return generalKeyValueMap;
			}
		}
		throw new TestEnvironmentConfigException("GeneralKeyValueMap with key[" + key + "] is not available on the list!");
	}

	/**
	 * @return the clusterCompatibilityVersion
	 */
	public ClusterCompatibilityVersion getClusterCompatibilityVersion() {
		return clusterCompatibilityVersion;
	}

	/**
	 * @param clusterCompatibilityVersion the clusterCompatibilityVersion to set
	 */
	@XmlElement(name="clusterCompatibilityVersion")
	public void setClusterCompatibilityVersion(
			ClusterCompatibilityVersion clusterCompatibilityVersion) {
		this.clusterCompatibilityVersion = clusterCompatibilityVersion;
	}

}