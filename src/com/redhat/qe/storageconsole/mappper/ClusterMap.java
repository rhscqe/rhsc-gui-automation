/**
 * 
 */
package com.redhat.qe.storageconsole.mappper;

import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 15, 2012
 */
public class ClusterMap extends BaseMap{
	/**
	 * 
	 */
	private static final String DEFAULT_RESOURCE_LOCATION = "System->Clusters";
	private String clusterName = null;
	private String clusterDescription = null;
	private String clusterCompatibilityVersion = "3.4";
	
	public static ClusterMap clusterMap(String clusterName){
		ClusterMap cluster = new ClusterMap();
		cluster.setResourceLocation(DEFAULT_RESOURCE_LOCATION);
		cluster.setClusterName(clusterName);
		cluster.setClusterDescription("Created by automation code");
		cluster.setClusterCompatibilityVersion(TestEnvironmentConfig.getTestEnvironment().getClusterCompatibilityVersion().toString());
		return cluster;
	}
	/**
	 * @return the name
	 */
	public String getClusterName() {
		return this.clusterName;
	}
	/**
	 * @param name the name to set
	 */
	public void setClusterName(String name) {
		this.clusterName = name;
	}
	/**
	 * @return the description
	 */
	public String getClusterDescription() {
		return this.clusterDescription;
	}
	/**
	 * @param description the description to set
	 */
	public void setClusterDescription(String description) {
		this.clusterDescription = description;
	}
	
	public String toString(){
		return "[Cluster Map] Name:"+this.getClusterName()+", Description:"+this.getClusterDescription()+", Resoure Location:"+this.getResourceLocation();		
	}
	/**
	 * @return the clusterCompatibilityVersion
	 */
	public String getClusterCompatibilityVersion() {
		return clusterCompatibilityVersion;
	}
	/**
	 * @param clusterCompatibilityVersion the clusterCompatibilityVersion to set
	 */
	public void setClusterCompatibilityVersion(
			String clusterCompatibilityVersion) {
		this.clusterCompatibilityVersion = clusterCompatibilityVersion;
	}
}
