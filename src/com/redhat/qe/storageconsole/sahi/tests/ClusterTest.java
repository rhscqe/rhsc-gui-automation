/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.redhat.qe.storageconsole.mappper.ClusterMap;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiClusterTasks;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiEventMessageTasks;
import com.redhat.qe.storageconsole.te.ClusterCompatibilityVersion;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 9, 2012
 */
public class ClusterTest extends SahiTestBase{
	private final String EVENT_MSG_CLUSTER_ADDED = "Host cluster .*. was added";
	private final String EVENT_MSG_CLUSTER_REMOVED = "Host cluster .*. was removed";
	private final String EVENT_MSG_CLUSTER_RENAMED = "Host cluster .*. was updated by";
	
	StorageSahiClusterTasks tasks = null;
	StorageSahiEventMessageTasks storageSahiMessageTasks = null;
	
	@BeforeMethod
	public void setUp() {
		tasks = new StorageSahiClusterTasks(browser);
		storageSahiMessageTasks = new StorageSahiEventMessageTasks(browser);
	}
	
	@Test (dataProvider="clusterCreationData")
	public void createCluster(ClusterMap cluster){
		Assert.assertTrue(tasks.createNewCluster(cluster), "Cluster["+cluster.getClusterName()+"] creation status!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_CLUSTER_ADDED.replace(".*.", cluster.getClusterName())));
	}
	
	@Test (dataProvider="clusterCreationData")
	public void checkRelevantHosts(ClusterMap cluster){
		Assert.assertTrue(tasks.checkRelevantHosts(cluster), "Status of hosts in Cluster["+cluster.getClusterName()+"]!");
	}	
	
	@Test (dataProvider="clusterCreationData")
	public void deleteCluster(ClusterMap cluster){
		Assert.assertTrue(tasks.removeCluster(cluster), "Cluster["+cluster.getClusterName()+"] deletion status");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_CLUSTER_REMOVED.replace(".*.", cluster.getClusterName())));
	}
	
	@Test (dataProvider="clusterNameAlreadyInUseData")
	public void createClusterNameAlreadyInUseNegative(ClusterMap cluster) {
		Assert.assertTrue(tasks.createNewCluster(cluster), "Cluster["+cluster.getClusterName()+"] aready exists!");
	}
	
	@Test
	public void removeClusterThatHasServers() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		ClusterMap cluster = (ClusterMap)getClusterCreationData()[0][0];
		cluster.setPositive(false);
		Assert.assertTrue(tasks.removeCluster(cluster), "Cluster["+cluster.getClusterName()+"] remove with servers!");
	}
	
	@Test
	public void renameCluster() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		ClusterMap cluster = (ClusterMap)getClusterCreationData()[0][0];
		cluster.setPositive(false);
		Assert.assertTrue(tasks.renameCluster(cluster), "Cluster["+cluster.getClusterName()+"] remove with servers!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_CLUSTER_RENAMED.replace(".*.", cluster.getClusterName())));
	}
	
	@Test
	public void clusterServicesTab() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		ClusterMap cluster = (ClusterMap)getClusterCreationData()[0][0];
		cluster.setPositive(false);
		Assert.assertTrue(tasks.validateClusterServicesTab(cluster), "Cluster["+cluster.getClusterName()+"] Services Tab!");
	}
	
	@DataProvider(name="clusterCreationData")
	public Object[][] getClusterCreationData() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(ClusterMap.clusterMap("automation_cluster1"));
		data.add(ClusterMap.clusterMap("automation_cluster2"));
		return this.convertListTo2dArray(data);
	}

	@DataProvider(name="clusterNameAlreadyInUseData")
	public Object[][] getClusterAlreadyExists() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<Object> data = new ArrayList<Object>();
		ClusterMap clusterObj = new ClusterMap();
		ClusterCompatibilityVersion clusterCompatibilityVersion = TestEnvironmentConfig.getTestEnvironemt().getClusterCompatibilityVersion();

		clusterObj.setResourceLocation("System->Clusters");
		clusterObj.setClusterName("automation_cluster1");
		clusterObj.setClusterDescription("Created by automation code");
		clusterObj.setErrorMsg("Cannot create Cluster. Cluster name is already in use.");
		clusterObj.setPositive(false);
		clusterObj.setClusterCompatibilityVersion(clusterCompatibilityVersion.toString());
		data.add(clusterObj); 
		
		return this.convertListTo2dArray(data);
	}
}
