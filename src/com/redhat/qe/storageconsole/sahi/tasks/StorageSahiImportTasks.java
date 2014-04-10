/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.storageconsole.helpers.AssertUtil;
import com.redhat.qe.storageconsole.helpers.RhscCleanerTool;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.elements.VolumeTable;
import com.redhat.qe.storageconsole.mappper.ClusterMap;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author mmahoney 
 * Nov 29, 2012
 */
public class StorageSahiImportTasks {

	/**
	 * 
	 */
	private static final String AUTHENTICATION_FAILED = "authentication failed";
	private static String NO_PEERS_IN_CLUSTER = "Number of Peers: 0";
	private static String NO_PEERS_PRESENT  = "No peers present";
	private static String PROBE_SUCCESSFUL = "success";
	private static String DETACH_SUCCESSFUL = "success";
	private static String SUCCESS = "success";
	
	private static String SERVERS_ALREADY_ADDED = "One or more servers are already";
	private static String INVALID_ROOT_PASSWORD = "make sure password is correct";
	
	StorageBrowser storageSahiTasks = null;
	StorageCLITasks storageCliTasks = null;
	StorageSahiClusterTasks clusterTasks = null;
	StorageSahiServerTasks serverTasks = null;
	StorageSahiVolumeTasks volumeTasks = null;
	
	public StorageSahiImportTasks(StorageBrowser tasks) {
		storageSahiTasks = tasks;
		storageCliTasks = new StorageCLITasks();
		clusterTasks = new StorageSahiClusterTasks(tasks);
		serverTasks = new StorageSahiServerTasks(tasks);
		volumeTasks = new StorageSahiVolumeTasks(tasks);
	}

	public boolean importExistingCluster(final ArrayList <ServerMap> serverMapList, int serversInCluster, final ArrayList <VolumeMap> volumeMapList) throws IOException, TestEnvironmentConfigException, JAXBException {
		ServerMap baseServer = serverMapList.get(0);
		
		/*
		 *  Setup
		 */
		Assert.assertTrue(createGlusterCluster(serverMapList, serversInCluster), "Gluster Cluster failed to create!");
		Assert.assertTrue(createVolumes(serverMapList, volumeMapList), "Gluster Volumes failed to create!");
		
		
		try{
			/*
			 *  Test
			 */
			if (!importCluster(serverMapList, serversInCluster)) {
				storageSahiTasks._logger.log(Level.WARNING, "Import Cluster failed!");
				removeGlusterCluster(serverMapList, serversInCluster);
			}
			
			if (!validateServersUp(serverMapList, serversInCluster)) {
				removeGlusterCluster(serverMapList, serversInCluster);
				Assert.fail("Servers not in Up status!");
			}
			
			Assert.assertTrue(verifyVolumes(volumeMapList), "Volumes not correct!");
		}catch (AssertionError e){
			throw e;
		}catch (Exception e){
			throw new RuntimeException(e);
		}finally{
			RhscCleanerTool.cleanup();
		}
		
		return true;
	}

	public boolean importClusterWithInvalidServerAddress(ArrayList <ServerMap> serverMapList) throws IOException {
		ServerMap server = serverMapList.get(0);
		
		if(!storageSahiTasks.selectPage(server.getResourceLocation() + "->Clusters")){
			return false;
		}
		
		if (!populateNewClusterDialogue(server)) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster ["+server.getClusterName()+"] failed to populate new cluster dialogue!");
			return false;
		}
		
		String actualFingerprint = storageSahiTasks.textarea("ClusterPopupView_glusterHostFingerprintEditor").getText();
		
		if ((actualFingerprint == null) || !actualFingerprint.contains(server.getErrorMsg())) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster  ["+server.getClusterName()+"] Server ["+server.getServerHostIP()+"] fingerprint Error!");
			return false;
		}
		
		cancelDialogue();
		
		return true;
	}

	public boolean importClusterWithInvalidRootPassword(ArrayList <ServerMap> serverMapList) throws IOException {
		ServerMap server = serverMapList.get(0);
		String savedPassword = server.getServerPassword();
		
		/*
		 *  Setup
		 */
		server.setServerPassword("invalidPassword321.");

		
		/*
		 *  Test
		 */
		storageSahiTasks.selectPage(server.getResourceLocation() + "->Clusters");
		
		Assert.assertTrue(populateNewClusterDialogue(server), "Cluster ["+server.getClusterName()+"] failed to populate new cluster dialogue!");
		
		storageSahiTasks.div("ClusterPopupView_OnSave").click();
		
		Assert.assertTrue(storageSahiTasks.div("/"+ INVALID_ROOT_PASSWORD + "/").exists(), "Cluster ["+server.getClusterName()+"] invalid root password message did not appear!");
		
		
		/*
		 * Cleanup
		 */
		cancelDialogue();   // Cancel New Cluster dialogue
		server.setServerPassword(savedPassword);
		
		return true;
	}
	
	public boolean importClusterWithInvalidServerPasswords(ArrayList <ServerMap> serverMapList) throws IOException {
		ServerMap server = serverMapList.get(0);
		
		// Test
		
		if(!storageSahiTasks.selectPage(server.getResourceLocation() + "->Clusters")) {
			removeGlusterCluster(serverMapList, 2);
			return false;
		}
		
		if (!populateNewClusterDialogue(server)) {
			removeGlusterCluster(serverMapList, 2);
			storageSahiTasks._logger.log(Level.WARNING, "Cluster ["+server.getClusterName()+"] failed to populate new cluster dialogue!");
			return false;
		}
		
		storageSahiTasks.div("ClusterPopupView_OnSave").click();
		
		// Sahi password index starts at "1".
		storageSahiTasks.password(1).setValue("invalidPassword321.");
			
		storageSahiTasks.closePopup("OK");
		
		// Error pop-up should be present
		
		if(!storageSahiTasks.div("/"+ AUTHENTICATION_FAILED + "/").exists()) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster ["+server.getClusterName()+"] invalid password disalogue did not appear!");
			removeGlusterCluster(serverMapList, 2);
			return false;
		}
		
		// Cleanup
		
		storageSahiTasks.div("Close").click();  // Close Error dialogue
		cancelDialogue();     		// Cancel New Servers dialogue
		
		// At this point, Cluster is expected to be created
		if (!waitForClusterToBeCreated(server)) {
			removeGlusterCluster(serverMapList, 2);
			return false;
		}
		
		if (!removeCluster(serverMapList.get(0))) {
			removeGlusterCluster(serverMapList, 2);
			return false;
		}
		
		return true;
	}
	
	public boolean importWithServersAlreadyInAnotherCluster(ArrayList <ServerMap> serverMapList) throws IOException {
		ServerMap server = serverMapList.get(0);
		
		// Test
		
		if(!storageSahiTasks.selectPage(server.getResourceLocation() + "->Clusters")) {
			removeGlusterCluster(serverMapList, 2);
			return false;
		}
		
		if (!populateNewClusterDialogue(server)) {
			removeGlusterCluster(serverMapList, 2);
			storageSahiTasks._logger.log(Level.WARNING, "Cluster ["+server.getClusterName()+"] failed to populate new cluster dialogue!");
			return false;
		}
		
		storageSahiTasks.div("ClusterPopupView_OnSave").click();
		
		if(!storageSahiTasks.div("/" + SERVERS_ALREADY_ADDED + "/").exists()) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster ["+server.getClusterName()+"] expected error message did not appear!");
			removeGlusterCluster(serverMapList, 2);
			return false;
		}
		
		// Cleanup
		
		cancelDialogue();
		
		// Cluster should not have been created
		if (storageSahiTasks.div(server.getClusterName()).exists()) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster ["+server.getClusterName()+"] was created!");
			removeCluster(serverMapList.get(0));
			return false;
		}
		
		return true;
	}
	
	/*
	 * Helper methods
	 */
	
	private boolean importCluster(ArrayList <ServerMap> serverMapList, int serversInCluster) throws IOException {
		ServerMap server = serverMapList.get(0);
		
		if(!storageSahiTasks.selectPage(server.getResourceLocation() + "->Clusters")){
			return false;
		}
		
		if (!populateNewClusterDialogue(server)) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster ["+server.getClusterName()+"] failed to populate new cluster dialogue!");
			return false;
		}
		

		String actualFingerprint = storageSahiTasks.textarea("ClusterPopupView_glusterHostFingerprintEditor").getText();
		String expectedFingerprint = getServerFingerprint(server);
			
		if ((actualFingerprint == null) || (expectedFingerprint == null) || !actualFingerprint.equals(expectedFingerprint)) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster  ["+server.getClusterName()+"] Server ["+server.getServerHostIP()+"] fingerprint not correct!");
			return false;
		}
		
		storageSahiTasks.div("ClusterPopupView_OnSave").click();
			
		if (!addServersDialog(serverMapList, serversInCluster)) {
			storageSahiTasks._logger.log(Level.WARNING, "Server import failed!");
			return false;
		}
		
		if (!waitForClusterToBeCreated(server)) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster not on list!");
			return false;
		}
		
		return true;
	}

	public boolean addServersDialog(ArrayList<ServerMap> serverMapList, int numberOfServers) throws IOException {
		
		if (!validateAddServersDialogue(serverMapList, numberOfServers)) {
			storageSahiTasks._logger.log(Level.WARNING, "Add Servers list not correct!");
			storageSahiTasks.closePopup("Cancel");
			return false;
		}
			
		for (int x = 0; x < numberOfServers; x++) {
			// Sahi password index starts at "1".
			storageSahiTasks.password(x + 1).setValue(serverMapList.get(x).getServerPassword());
		}
			
		storageSahiTasks.closePopup("OK");

		if(storageSahiTasks.div("Error").exists()) {
			storageSahiTasks._logger.log(Level.WARNING, "Error encountered when adding Servers!");
			// removeGlusterCluster(serverMapList, serversInCluster);
			return false;
		}
		
		return true;
	}

	public boolean importServers(String clusterName, ArrayList<ServerMap> serverMapList) throws IOException {
		
		if(!storageSahiTasks.selectPage("System->Clusters")){
			return false;
		}
        storageSahiTasks.clickRefresh("Cluster");
        
        clusterTasks.selectClusterRow(clusterName);
        storageSahiTasks.div("General").click();
        Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(storageSahiTasks.link("Import")), 5), "Import link did not appear");
        storageSahiTasks.link("Import").click();
        
        Assert.assertTrue(addServersDialog(serverMapList, serverMapList.size()));        
		return validateServersUp(serverMapList, serverMapList.size());
	}
	
	private boolean populateNewClusterDialogue(ServerMap server) {
		
		if (storageSahiTasks.div(server.getClusterName()).exists()) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster ["+server.getClusterName()+"] is already on the list!");
			return false;
		}
		
		storageSahiTasks.div("MainTabClusterView_table_New").click();
		storageSahiTasks.select("ClusterPopupView_versionEditor").choose(server.getClusterCompatibilityVersion());
		storageSahiTasks.textbox("ClusterPopupView_nameEditor").setValue(server.getClusterName());
		storageSahiTasks.textbox("ClusterPopupView_descriptionEditor").setValue(server.getClusterDescription());
		storageSahiTasks.checkbox("ClusterPopupView_isImportGlusterConfiguration").check();
		storageSahiTasks.textbox("ClusterPopupView_glusterHostAddressEditor").setValue(server.getServerHostIP());
		storageSahiTasks.password("ClusterPopupView_glusterHostPasswordEditor").setValue(server.getServerPassword());
		
		return true;
	}
	
	private boolean createGlusterCluster(ArrayList <ServerMap> serverMapList, int serversInCluster) throws IOException {
		String glusterCommand = null;
		String commandOutput = null;
		
		if (serverMapList.size() < serversInCluster) {
			storageSahiTasks._logger.log(Level.WARNING, "There are not [" + serversInCluster + "] servers in list to create Gluster cluster!");
			return false;
		}
		
		ServerMap server1 = serverMapList.get(0);
		ServerMap serverx = null;
		
		Assert.assertFalse(doesGlusterServerHavePeers(server1),"Server [" + server1.getServerHostIP() + "] should have no peers" );

		for (int x = 0; x < (serversInCluster - 1); x++) {
			
			serverx = serverMapList.get(x + 1);

			// Add a peer(s) (create the Gluster cluster)
			glusterCommand = storageCliTasks.commandGlusterPeerProbe + serverx.getServerHostIP();
			commandOutput = storageCliTasks.runGenericCommand(server1.getServerHostIP(), server1.getServerUsername(), server1.getServerPassword(), glusterCommand);
			if (!commandOutput.contains(PROBE_SUCCESSFUL)) {
				storageSahiTasks._logger.log(Level.WARNING, "Server [" + server1.getServerHostIP() + "] probe failed!");
				return false;
			} 
		
			// Validate that the peer added is the expected node in the Cluster (i.e. was the correct node added to the Cluster)
			glusterCommand = storageCliTasks.commandGlusterPeerStatus;
			commandOutput = storageCliTasks.runGenericCommand(server1.getServerHostIP(), server1.getServerUsername(), server1.getServerPassword(), glusterCommand);
			if (!commandOutput.contains(serverx.getServerHostIP())) {
				storageSahiTasks._logger.log(Level.WARNING, "Server [" + serverx.getServerHostIP() + "] not in cluster!");
				return false;
			}
		}
		
		return true;
	}
	
	private boolean removeGlusterCluster(ArrayList <ServerMap> serverMapList, int serversInCluster) throws IOException {
		String glusterCommand = null;
		String commandOutput = null;
		
		if (serverMapList.size() < serversInCluster) {
			storageSahiTasks._logger.log(Level.WARNING, "There are not [" + serversInCluster + "] servers in list to remove Gluster cluster!");
			return false;
		}
		
		ServerMap server1 = serverMapList.get(0);
		ServerMap serverx = null;
		
		for (int x = 0; x < (serversInCluster - 1); x++) {
			serverx = serverMapList.get(x + 1);
			
			// Detach a peer(s)
			glusterCommand = storageCliTasks.commandGlusterPeerDetach + serverx.getServerHostIP();
			commandOutput = storageCliTasks.runGenericCommand(server1.getServerHostIP(), server1.getServerUsername(), server1.getServerPassword(), glusterCommand);
			if ((commandOutput == null) || !commandOutput.contains(DETACH_SUCCESSFUL)) {
				storageSahiTasks._logger.log(Level.WARNING, "Server [" + server1.getServerHostIP() + "] detach failed!");
				return false;
			} 
		}
		
		return true;
	}

	private boolean validateServersUp(ArrayList <ServerMap> serverMapList, int nodesInCluster) {
		
		if (serverMapList.size() < nodesInCluster) {
			storageSahiTasks._logger.log(Level.WARNING, "There are not enough servers!");
			return false;
		}
		
		if(!storageSahiTasks.selectPage(serverMapList.get(0).getResourceLocation() + "->Hosts")){
			return false;
		}
		
		for (int x = 0; x < nodesInCluster; x++) {
			if(!serverTasks.waitForServerRowExist(serverMapList.get(x)))
				return false;
		    if (!serverTasks.waitForServerUpStatus(serverMapList.get(x))) {
		    	return false;
		    }
		}
		
		return true;
	}
	
	private boolean removeVolumes(ArrayList <VolumeMap> volumeMapList) {
		
		for (VolumeMap volumeMap : volumeMapList) {
			volumeMap.setResourceLocation("System->Volumes");
			Assert.assertTrue(volumeTasks.stopVolume(volumeMap), "Volume [" + volumeMap.getVolumeName() + "] stop failed!");

			Assert.assertTrue(volumeTasks.removeVolume(volumeMap), "Volume [" + volumeMap.getVolumeName() + "] remove failed!");
		}
		return true;
	}
	
	private boolean removeServers(ArrayList <ServerMap> serverMapList, int serversInCluster) {
		ServerMap server = null;
		
		if (serverMapList.size() < serversInCluster) {
			storageSahiTasks._logger.log(Level.WARNING, "There are not [" + serversInCluster + "] servers in list to remove from cluster!");
			return false;
		}

		for (int x = 0; x < serversInCluster; x++) {
			server = serverMapList.get(x);
			server.setResourceLocation("System->Hosts");
		    if (!serverTasks.removeServer(server)) {
		    	storageSahiTasks._logger.log(Level.WARNING, "Server [" + server.getServerName() + "] failed to remove!");
		    	return false;
		    }
		}
		
		return true;
	}
	
	private boolean removeCluster(ServerMap server) {
		ClusterMap cluster = new ClusterMap();
		cluster.setClusterName(server.getClusterName());
		cluster.setResourceLocation("System->System->Clusters");
		
		if (!clusterTasks.removeCluster(cluster)) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster [" + cluster.getClusterName() + "] failed to remove!");
			return false;
		}
		
		return true;
	}
	
	private boolean doesGlusterServerHavePeers(ServerMap server) throws IOException {
		String glusterCommand = null;
		String commandOutput = null;
		
		glusterCommand = storageCliTasks.commandGlusterPeerStatus;
		commandOutput = storageCliTasks.runGenericCommand(server.getServerHostIP(), server.getServerUsername(), server.getServerPassword(), glusterCommand);
		if (commandOutput.contains(NO_PEERS_IN_CLUSTER) || commandOutput.contains(NO_PEERS_PRESENT)) {
			storageSahiTasks._logger.log(Level.INFO, "Server [" + server.getServerHostIP() + "] does not have peers!");
			return false;
		}
		
		storageSahiTasks._logger.log(Level.INFO, "Server [" + server.getServerHostIP() + "] has peers!");
		
		return true;
	}
	
	private String getServerFingerprint(ServerMap server) throws IOException {
		String fingerprint = null;
		String commandOutput = storageCliTasks.runGenericCommand(server.getServerHostIP(), server.getServerUsername(), server.getServerPassword(), storageCliTasks.commandGetServerFingerprint);
		
		// Example: "stderr:::stdout:2048 2b:11:18:7b:79:8e:04:f3:b8:48:6c:2b:bb:4e:62:84 /etc/ssh/ssh_host_rsa_key.pub (RSA)"
		commandOutput = commandOutput.substring(commandOutput.indexOf("stdout:"));
		fingerprint = commandOutput.substring(commandOutput.indexOf(' '), commandOutput.lastIndexOf(" /"));
		return fingerprint.trim();
	}
	
	private boolean validateAddServersDialogue(ArrayList <ServerMap> serverMapList, int serversInCluster) throws IOException {
		String actualIP = null;
		String expectedIP = null;
		String actualFingerprint = null;
		String expectedFingerprint = null;
		String reference = "MultipleHostsPopupView_hostsTable";
		ServerMap server = null;
		
		int actualServersCount = storageSahiTasks.div("/" + reference + "_col1_row" + "/").countSimilar();
		
		if (actualServersCount != serversInCluster) {
			storageSahiTasks._logger.log(Level.WARNING, "Incorrect number of Servers in Add Servers list!");
			return false;
		}
		
		for (int x = 0; x < serversInCluster; x++) {
			boolean foundIPInList = false;
			server = serverMapList.get(x);
			
			actualIP = storageSahiTasks.div("/" + reference + "_col1_row" + x + "/").getText();
			
			for (int y = 0; y < serversInCluster; y++) {
				expectedIP = serverMapList.get(y).getServerHostIP();
				if (actualIP.equals(expectedIP)) {
					actualFingerprint = storageSahiTasks.div("/" + reference + "_col3_row" + y + "/").getText();
					foundIPInList = true;
					break;
				}
			}
			
			if (!foundIPInList) {
				storageSahiTasks._logger.log(Level.INFO, "Server [" + actualIP + "] IP not in expected Server list!");
				return false;
			} else {
			
				expectedFingerprint = getServerFingerprint(server);
			
				if (!actualFingerprint.contains(expectedFingerprint)) {
					storageSahiTasks._logger.log(Level.INFO, "Server [" + server.getServerHostIP() + "] Fingerprint not correct!");
					return false;
				}
			}
		}
		
		return true;
	}

	private boolean createVolumes(ArrayList <ServerMap> serverMapList, ArrayList <VolumeMap> volumeMapList) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException {
		String volumeStartCommand = storageCliTasks.commandGlusterVolumeStart;
		ServerMap server1 = serverMapList.get(0);
		
		for (VolumeMap volumeMap : volumeMapList) {		
			Assert.assertTrue(storageCliTasks.glusterCreateVolume(volumeMap ), "Volume [" + volumeMap.getVolumeName() + "] volume create failed!");
			
			if (volumeMap.getVolumeStart()) {
				volumeStartCommand = volumeStartCommand + volumeMap.getVolumeName();
				String commandOutput = storageCliTasks.runGenericCommand(server1.getServerHostIP(), server1.getServerUsername(), server1.getServerPassword(), volumeStartCommand);
				Assert.assertTrue((commandOutput == null) || commandOutput.contains(SUCCESS), "Volume [" + volumeMap.getVolumeName() + "] volume start failed!");
			}
		}
		
		return true;
	}
	
	private boolean verifyVolumes(final ArrayList <VolumeMap> volumeMapList) {
		LinkedList<HashMap<String, String>> volumesTable = clusterTasks.readVolumesTable();
		
		for (VolumeMap volumeMap : volumeMapList) {
			boolean haveIt = false;
			for(HashMap<String, String> volume : volumesTable) {
				String actualVolumeName = volume.get(GuiTables.NAME);
				VolumeTable table = new VolumeTable(storageSahiTasks);
				String actualVolumeStatus = (table.isStatusDown(table.getFirstRowIndexThatContainsText(volumeMap.getVolumeName()))) ? GuiTables.Status.DOWN.get() : GuiTables.Status.UP.get();
				String expectedVolumeStatus = ((volumeMap.getVolumeStart()) ? GuiTables.Status.UP.get() : GuiTables.Status.DOWN.get());
				if (volumeMap.getVolumeName().equals(actualVolumeName) && expectedVolumeStatus.equalsIgnoreCase(actualVolumeStatus)) {
					// storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] on list!");
					haveIt = true;
					break;
				}
            }
            if (!haveIt) {
            	storageSahiTasks._logger.log(Level.WARNING, "Volume [" + volumeMap.getVolumeName() + "] not correct on list!");
            	return false;
            }
		}
		
		return true;
	}
	
		
	private boolean waitForClusterToBeCreated(ServerMap server) {
		int wait = 1000;
		int retryCount = 15;
		
		for (int x = 0; x < retryCount; x++) {
			if (storageSahiTasks.div(server.getClusterName()).exists()) {
				break;
			} else {
				storageSahiTasks.wait(wait, retryCount, x);
				storageSahiTasks.clickRefresh("Cluster");
			}
		}
			
		if (!storageSahiTasks.div(server.getClusterName()).exists()) {
			storageSahiTasks._logger.log(Level.WARNING, "Cluster [" + server.getClusterName() + "] did not import!");
			return false;
		}
		
		return true;
	}
	
	private void cancelDialogue() {
		for(int x = 0; x < 5; x++) {
			if(storageSahiTasks.div("Cancel").exists()) {
				storageSahiTasks._logger.log(Level.FINE, "Attempt: [" + x + "] Cancel.");
				storageSahiTasks.div("Cancel").click();
			} else {
				break;
			}
		}
	}
}
