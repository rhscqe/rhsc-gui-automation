package com.redhat.qe.storageconsole.sahi.tasks;

import static com.redhat.qe.storageconsole.helpers.AssertUtil.failTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.redhat.qe.storageconsole.helpers.PagePanels;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.elements.ClusterTable;
import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.elements.tab.ClusterTab;
import com.redhat.qe.storageconsole.helpers.pages.components.MainTabPanel;
import com.redhat.qe.storageconsole.mappper.ClusterMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.Sshable;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

public class StorageSahiClusterTasks {
	private static final int TEN_ATTEMPTS = 10;

	GuiTables guiTables = new GuiTables();
	
	StorageBrowser storageSahiTasks = null;
	StorageCLITasks storageCLITasks = null;
	StorageSahiEventMessageTasks storageSahiMessageTasks = null;
	
    private static final String SERVICE_NFS = "NFS";
    private static final String SERVICE_SHD = "SHD";

	public StorageSahiClusterTasks(StorageBrowser tasks) {
		storageSahiTasks = tasks;
		storageCLITasks = new StorageCLITasks();
		storageSahiMessageTasks = new StorageSahiEventMessageTasks(storageSahiTasks);
	}
	
	//-----------------------------------------------------------------------------
	// Create/remove Cluster
	//-----------------------------------------------------------------------------

	/*
	 * Creates Cluster with the specified details
	 */
	public boolean createNewCluster(ClusterMap cluster){
		if(!storageSahiTasks.selectPage("Clusters")){
			return false;
		}
		
		waitForPageTableToAppear();
		
		if(cluster.isPositive()) {
			if (storageSahiTasks.div(cluster.getClusterName()).exists()) {
				storageSahiTasks._logger.log(Level.WARNING, "Cluster ["+cluster.getClusterName()+"] is already on the list!");
				return false;
			}
		} else {
			if (!storageSahiTasks.div(cluster.getClusterName()).exists()) {
				storageSahiTasks._logger.log(Level.WARNING, "Pre-Requirement not met: Cluster ["+cluster.getClusterName()+"] not available on the available list!");
				return false;
			}
		}
		
		storageSahiTasks.div("MainTabClusterView_table_New").click();
		storageSahiTasks.textbox("ClusterPopupView_nameEditor").setValue(cluster.getClusterName());
		storageSahiTasks.textbox("ClusterPopupView_descriptionEditor").setValue(cluster.getClusterDescription());
		storageSahiTasks.select("ClusterPopupView_versionEditor").choose(cluster.getClusterCompatibilityVersion());
		storageSahiTasks.div("ClusterPopupView_OnSave").click();
		
		if (cluster.isPositive()) {
			storageSahiTasks.div("GuidePopupView_Cancel").click();
			storageSahiTasks.clickRefresh("Cluster");
			storageSahiTasks._logger.log(Level.INFO, "Table: "+new ClusterTab(storageSahiTasks).getTable().getData());
			
			if(!storageSahiTasks.div(cluster.getClusterName()).exists()){
				storageSahiTasks._logger.log(Level.WARNING, "Cluster["+cluster.getClusterName()+"] is not available on the list!");
				return false;
			}
		} else {
		    if(storageSahiTasks.div("/" + cluster.getErrorMsg() + "/i").exists()) {
		    	storageSahiTasks._logger.log(Level.FINE, "Volume ["+cluster.getClusterName()+"] creation error pop-up appeared!");
		    	storageSahiTasks.closePopup("Close");
		    	storageSahiTasks.div("ClusterPopupView_Cancel").click();
			    return true;
		    } else {
		    	storageSahiTasks._logger.log(Level.WARNING, "Volume ["+cluster.getClusterName()+"] creation error pop-up did not appear!");
			    return false;
		    }
		}		
		
		return true;
	}

	/**
	 * 
	 */
	private void waitForPageTableToAppear() {
		boolean result = WaitUtil.waitUntil(new Predicate<Integer>() {
			public boolean apply(Integer att){
				return storageSahiTasks.div("/" + GuiTables.CLUSTERS_TABLE_REFERENCE + "/").isVisible();
			}
		}, 10, "Cluster Table is visible");
		Assert.assertTrue(result, "cluster table did not appear");
	}

	/*
	 * Removes the specified Cluster
	 */
	public boolean removeCluster(ClusterMap cluster){
		if(!storageSahiTasks.selectPage("Clusters")){
			return false;
		}
		storageSahiTasks._logger.log(Level.FINE,"In remove cluster");
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(storageSahiTasks.div(cluster.getClusterName())), 10), "Cluster [" + cluster.getClusterName() + "] did not appear!");
		storageSahiTasks.div(cluster.getClusterName()).in(new MainTabPanel(storageSahiTasks).getElementStub()).click();
		removeButton().click();
		storageSahiTasks._logger.log(Level.FINE,"Clicked Remove");

		storageSahiTasks.div("RemoveConfirmationPopupView_OnRemove").click();	
		
		if (cluster.isPositive()) {
			storageSahiTasks.clickRefresh("Cluster");
			if(storageSahiTasks.div(cluster.getClusterName()).in(new PagePanels(storageSahiTasks).getRHSView()).exists()){
				storageSahiTasks._logger.log(Level.WARNING, "Cluster["+cluster.getClusterName()+"] is available on the list!");
				return false;
			}
		} else {
			if (!storageSahiTasks.div("/"+ "Error" + "/").exists()) {
				storageSahiTasks._logger.log(Level.WARNING, "Cluster["+cluster.getClusterName()+"] expected Error!");
				return false;
			}
			
			storageSahiTasks.closePopup("Close");
		}
		return true;
	}

	/**
	 * @return
	 */
	private ElementStub removeButton() {
		return storageSahiTasks.div("MainTabClusterView_table_Remove");
	}
	
	//-------------------------------------------------------------------------------------
	// Reads the list of servers existing in the setup at any time.
	//-------------------------------------------------------------------------------------
	public LinkedList<HashMap<String, String>> readServersTable() {
		storageSahiTasks.selectPage("System->Hosts");
		storageSahiTasks.clickRefresh("Host");
		LinkedList<HashMap<String, String>> serversTable = GuiTables.getServersTable(storageSahiTasks);
		return serversTable;
	}
	
    public LinkedList<HashMap<String, String>> readVolumesTable() {
        storageSahiTasks.selectPage("System->System->Volumes");
        storageSahiTasks.clickRefresh("Volumes");
        LinkedList<HashMap<String, String>> volumesTable = GuiTables.getVolumesTable(storageSahiTasks);
        return volumesTable;
    }

	//-------------------------------------------------------------------------------------
	// To check the relevance of hosts appearing in the drop-down list for selection of hosts while adding bricks.
	//-------------------------------------------------------------------------------------
	public boolean checkRelevantHosts(ClusterMap clusterMap){
		LinkedList<HashMap<String, String>> serversTable = readServersTable();// Read details of all servers present in the setup
		storageSahiTasks.selectPage("System->System->Volumes");
		storageSahiTasks.div("MainTabVolumeView_table_Create_Volume").click();
		storageSahiTasks.select("VolumePopupView_cluster").choose(clusterMap.getClusterName());
		if( serverTableContainsClusterName(serversTable, clusterMap.getClusterName())){
			storageSahiTasks._logger.log(Level.INFO, "Checking Add Brick Dialog for Host");
			if(!checkIfThereAreRelevantHostsInAddBrickDialogForCluster(clusterMap.getClusterName(), serversTable)){
				return false;
			}
		}else{
			storageSahiTasks._logger.log(Level.INFO, "Did not Check Add Brick Dialog for Host");
		}
		storageSahiTasks.div("VolumePopupView_Cancel").click();
		storageSahiTasks._logger.log(Level.INFO, "All servers appearing in the servers drop-down for add-brick are relevant hosts.");
	return true;
	}
	

	private boolean serverTableContainsClusterName(LinkedList<HashMap<String, String>> serversTable, final String clusterName) {
		Collection<HashMap<String, String>> rowsWithClusterName = Collections2.filter(serversTable, new Predicate<HashMap<String,String>>() {public boolean apply (HashMap<String,String> row){return row.get(GuiTables.CLUSTER).equals(clusterName);}});
		if (rowsWithClusterName.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkIfThereAreRelevantHostsInAddBrickDialogForCluster(String clustername, LinkedList<HashMap<String, String>> serversTable){
		storageSahiTasks.div("VolumePopupView_addBricksButton").click();
		String serverCSV = storageSahiTasks.select("AddBrickPopupView_serverEditor").getText();//Gets all the servers from the drop down, comma-separated
		String[] serverList = serverCSV.split(",");
		storageSahiTasks._logger.log(Level.FINE, "Checking for relevance of hosts appearing in the drop-down for selection of servers while adding bricks.");
		for(String server: serverList){
			storageSahiTasks._logger.log(Level.FINE, "Server being checked : "+server);
			for(HashMap<String, String> row : serversTable){
				if(row.get(GuiTables.HOST_IP).equals(server)){
					if(!row.get(GuiTables.CLUSTER).equals(clustername)){
						storageSahiTasks._logger.log(Level.WARNING, "Server["+server+"] not part of cluster["+clustername+"!");
						return false;
					} else {
						if(!row.get(GuiTables.STATUS).equals(GuiTables.Status.UP.get())){
							storageSahiTasks._logger.log(Level.WARNING, "Server["+server+"] not \"UP\"!");
							return false;
						}
					}
				}
			}
		}
		storageSahiTasks.div("AddBrickPopupView_Cancel").click();
		return true;
	}
	
	public boolean renameCluster(ClusterMap cluster) {
		String expectedClusterName = cluster.getClusterName() + System.currentTimeMillis();
		
		if (!editClusterName(cluster, cluster.getClusterName(), expectedClusterName)) {
			return false;
		}
		
		if(!storageSahiTasks.selectPage("System->Hosts")) {
			return false;
		}
		
		// Validate cluster name change for each server
		LinkedList<HashMap<String, String>> serversTable = readServersTable();
		for(HashMap<String, String> row : serversTable) {
			String actualClusterName = row.get(GuiTables.CLUSTER);
			// Handle the case where a server may be a member of a different cluster
			if(actualClusterName.contains(cluster.getClusterName())) {
				if (!actualClusterName.equals(expectedClusterName)) {
					storageSahiTasks._logger.log(Level.WARNING, "Server[" + row.get(GuiTables.NAME) + "] does not contain expected new cluster name [" + expectedClusterName + "]!");
					editClusterName(cluster, cluster.getClusterName(), cluster.getClusterName());
					return false;
				}
			}
		}
		
		editClusterName(cluster, expectedClusterName, cluster.getClusterName());
		
		return true;
	}
	
    public boolean validateClusterServicesTab(ClusterMap cluster) throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
        ElementStub nearReferenceForClusterServicesTable = storageSahiTasks.tableHeader("Host");
        boolean expectedSHDVolumeIsPresent = false;
        boolean actualSHDVolumeIsPresent = false;
        LinkedList<HashMap<String, String>> clusterServicesTable = null;
        int serviceTableLoadRetryCount = 20;

        LinkedList<HashMap<String, String>> volumesTable = readVolumesTable();

        // Cluster Services will show info for SHD Volumes only when there is an existing Replicate and/or Distributed Replicate
        for (HashMap<String, String> volumeRow : volumesTable) {
        	String volumeType = volumeRow.get(GuiTables.VOLUME_TYPE);
            if (volumeType.equals(VolumeMap.VolumeType.REPLICATE.toString()) || (volumeType.equals(VolumeMap.VolumeType.DISTRIBUTED_REPLICATE.toString()))) {
            	expectedSHDVolumeIsPresent = true;
                break;
            }
        }

        LinkedList<HashMap<String, String>> serversTable = readServersTable();

        if(!storageSahiTasks.selectPage(cluster.getResourceLocation())) {
        	return false;
        }

        storageSahiTasks.clickRefresh("Cluster");
        selectClusterRow(cluster.getClusterName());
        storageSahiTasks.div("Services").click();

        waitForElementToAppear(nearReferenceForClusterServicesTable);

        String actualPID = null;
        String actualService = null;
        String actualIP = null;
        String userName = null;
        String password = null;
        List<Server> testEnvironmentConfigServerList = TestEnvironmentConfig.getTestEnvironemt().getServers();

        for (HashMap<String, String> serverRow: serversTable) {
            actualSHDVolumeIsPresent = false;

            // Filter for this current Server
            storageSahiTasks.select(0).near(storageSahiTasks.label("Host")).choose(serverRow.get(GuiTables.HOST_IP));
            for (int x = 0; x < 3; x++) {
                storageSahiTasks.div("Filter").near(storageSahiTasks.label("Host")).click();
            }

            boolean timedOut = true;

            // Wait for services table to load
            for (int retryCount = 0; retryCount < serviceTableLoadRetryCount; retryCount++) {
                if (storageSahiTasks.div(serverRow.get(GuiTables.HOST_IP)).exists()) {
                	timedOut = false;
                    break;
                } else {
                    storageSahiTasks._logger.log(Level.FINE, "Attempt: [" + (retryCount+1) + " of " + serviceTableLoadRetryCount + "]");
                    storageSahiTasks.waitFor(1000);
                }
            }

            if (timedOut) {
            	failTest("Services table did not load!");
            	return false;
            }

            clusterServicesTable = GuiTables.getClusterServicesTable(storageSahiTasks, nearReferenceForClusterServicesTable);

            for (HashMap<String, String> clusterServiceRow : clusterServicesTable) {
            	String expectedPID = null;
            	actualIP = clusterServiceRow.get(GuiTables.CLUSTER_SERVICES_HOST);
            	actualService = clusterServiceRow.get(GuiTables.CLUSTER_SERVICES_SERVICE);
            	actualPID = clusterServiceRow.get(GuiTables.CLUSTER_SERVICES_PROCESS_ID).trim();

            	storageSahiTasks._logger.log(Level.INFO, "Server: " + actualIP + "  service: " + actualService + "  actualPID: " + actualPID);

            	if (!actualIP.equals(serverRow.get(GuiTables.HOST_IP))) {
            		failTest("Server [" + actualIP + "] not correct!");
            		return false;
            	}

            	for(Sshable server : testEnvironmentConfigServerList){
            		if (actualIP.equals(server.getHostname())) {
            			userName = server.getLogin();
            			password = server.getPassword();
            			break;
            		}
            	}

            	if ((userName == null) || (password == null)) {
            		failTest("Server [" + actualIP + "] not able to get username/password!");
            		return false;
            	}

            	if (actualService.equals(SERVICE_NFS)) {
            		expectedPID = storageCLITasks.getServerGlusterNFSProcessID(actualIP, userName, password);
            	} else if (actualService.equals(SERVICE_SHD)) {
            		expectedPID = storageCLITasks.getServerGlusterSHDProcessID(actualIP, userName, password);
            		actualSHDVolumeIsPresent = true;
            	} else {
            		failTest("Server [" + actualIP + "] Service [" + actualService + "] not correct!");
            		return false;
            	}

            	if (!actualPID.equals(expectedPID)) {
            		String message = String.format("Server [%s] process ID not correct. Actual PID [%s], Expected PID [%s].", actualIP, expectedPID, actualPID);
            		failTest(message);
            		return false;
            	}
            }

            if (expectedSHDVolumeIsPresent && !actualSHDVolumeIsPresent) {
            	failTest("Server [" + actualIP + "] SHD not present as expected!");
            	return false;
            }
        }
        return true;
	}
	
    public void selectClusterRow(String clusterName) {
        ElementStub clusterTable = storageSahiTasks.table("[0]").near(storageSahiTasks.tableHeader("Compatibility Version[1]"));
        ElementStub currentClusterElement = storageSahiTasks.div(clusterName).in(clusterTable);
        waitForElementToAppear(currentClusterElement);
        currentClusterElement.click();
    }
    
    private boolean editClusterName(ClusterMap cluster, String currentCluster, String newClusterName) {

        if(!storageSahiTasks.selectPage(cluster.getResourceLocation())) {
                return false;
        }
        ElementStub clusterTable = storageSahiTasks.table("[0]").near(storageSahiTasks.tableHeader("Compatibility Version[1]"));
        ElementStub currentClusterElement = storageSahiTasks.div(currentCluster).in(clusterTable);
        Assert.assertTrue(waitForElementToAppear(currentClusterElement), "Cluster table is visible");
        
        selectClusterRow(currentCluster);
        storageSahiTasks.div("MainTabClusterView_table_Edit").click();
        storageSahiTasks.textbox("ClusterPopupView_nameEditor").setValue(newClusterName);
        storageSahiTasks.div("ClusterPopupView_OnSave").click();

        return true;
    }


	/**
	 * @param currentCluster
	 */
	private boolean waitForElementToAppear(ElementStub elem) {
		for(int numTries=0; numTries< TEN_ATTEMPTS; numTries ++){
			if(elem.isVisible()){
				return true;
			}
		}
		return false;
	}
}
