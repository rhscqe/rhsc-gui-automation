package com.redhat.qe.storageconsole.sahi.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiImportTasks;
import com.redhat.qe.storageconsole.te.Sshable;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

public class ImportTest extends SahiTestBase {
	StorageSahiImportTasks tasks = null;
	
	@BeforeMethod
	public void setUp() {
		tasks = new StorageSahiImportTasks(browser);
	}
	
	@Test
	public void importClusterWithNoPeers() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		Assert.assertTrue(tasks.importExistingCluster(getTwoNodeClusterData(), 1, new ArrayList<VolumeMap>()), "Import one node cluster!");
	}
	
	@Test
	public void importTwoNodeCluster() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		Assert.assertTrue(tasks.importExistingCluster(getTwoNodeClusterData(), 2, new ArrayList<VolumeMap>()), "Import two node cluster!");
	}
	
	@Test
	public void importTwoNodeClusterWithVolumes() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		Assert.assertTrue(tasks.importExistingCluster(getTwoNodeClusterData(), 2, getVolumeData()), "Import two node cluster with volumes!");
	}
	
	@Test
	public void importClusterWithInvalidServerAddress() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		Assert.assertTrue(tasks.importClusterWithInvalidServerAddress(getInvalidServerAddressData()), "Invalid Server Address!");
	}
	
	@Test
	public void importClusterWithInvalidRootPassword() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		Assert.assertTrue(tasks.importClusterWithInvalidRootPassword(getTwoNodeClusterData()), "Invalid Root Password!");
	}
	
	@Test
	public void importClusterWithInvalidAddServersPassword() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		Assert.assertTrue(tasks.importClusterWithInvalidServerPasswords(getTwoNodeClusterData()), "Invalid Passwords!");
	}
	
	@Test
	public void importWithServersAlreadyInAnotherCluster() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		Assert.assertTrue(tasks.importWithServersAlreadyInAnotherCluster(getTwoNodeClusterData()), "Servers already in another cluster!");
	}
	
	/*
	 * Test Data
	 */
	
	private ArrayList <ServerMap> getTwoNodeClusterData() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList <ServerMap> serverMapList = new ArrayList <ServerMap>();
		Sshable server = null;
		
		for (int x = 0; x < 2; x++) {
			ServerMap serverObj = new ServerMap();
			server = TestEnvironmentConfig.getTestEnvironemt().getServers().get(x);

			serverObj.setResourceLocation("System");   // Will be going to Clusters and Servers tabs
			serverObj.setClusterName("automation_import2nodecluster");
			serverObj.setClusterDescription("Automation - Import 2 Node Cluster");
			serverObj.setServerHostIP(server.getHostname());
			serverObj.setServerName(server.getHostname()); // Make same as Host IP because there is no ability to add a Host Name for Import
			serverObj.setServerUsername(server.getLogin());
			serverObj.setServerPassword(server.getPassword());
			serverMapList.add(serverObj);
		}
		
		return serverMapList;
	}
	
	private ArrayList <ServerMap> getInvalidServerAddressData() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList <ServerMap> serverMapList = new ArrayList <ServerMap>();
		
		Sshable server = TestEnvironmentConfig.getTestEnvironemt().getServers().get(0);
		
		ServerMap serverObj = new ServerMap();
		serverObj.setPositive(false);
		serverObj.setResourceLocation("System");   // Will be going to Clusters and Servers tabs
		serverObj.setClusterName("automation_invalidServerAddresscluster");
		serverObj.setClusterDescription("Automation - Invalid Server Address");
		serverObj.setServerHostIP("1234");
		serverObj.setServerName(serverObj.getServerHostIP()); // Make same as Host IP because there is no ability to add a Host Name for Import
		serverObj.setServerUsername(server.getLogin());
		serverObj.setServerPassword(server.getPassword());
		serverObj.setErrorMsg("Error");
		serverMapList.add(serverObj);
		
		return serverMapList;
	}
	
	public ArrayList <VolumeMap> getVolumeData() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<VolumeMap> volumeMapList = new ArrayList<VolumeMap>();
		
		VolumeMap volumeMap = new VolumeMap();
		volumeMap.setVolumeName("import-volume-distribute");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-distribute-1}{server24=bricks-distribute-1}");
		volumeMap.setSpecialCount(0);
		volumeMap.setVolumeStart(true);
		volumeMapList.add(volumeMap);
		
		volumeMap = new VolumeMap();
		volumeMap.setVolumeName("import-distributed-stripe");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTED_STRIPE.toString());
		volumeMap.setServers("{server23=bricks-distributed-stripe-1}{server24=bricks-distributed-stripe-1}");
		volumeMap.setSpecialCount(4);
		volumeMapList.add(volumeMap);	
		
		return volumeMapList;
	}
}
