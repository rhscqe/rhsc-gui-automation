package com.redhat.qe.storageconsole.sahi.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import net.sf.sahi.client.ExecutionException;

import org.testng.Assert;

import static com.redhat.qe.storageconsole.helpers.AssertUtil.*;

import com.google.common.base.Predicate;
import com.redhat.qe.storageconsole.helpers.PagePanels;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.elements.AssignTagDialog;
import com.redhat.qe.storageconsole.helpers.elements.ContextMenu;
import com.redhat.qe.storageconsole.helpers.elements.Row;
import com.redhat.qe.storageconsole.helpers.elements.ServerTable;
import com.redhat.qe.storageconsole.helpers.elements.tab.ServerTab;
import com.redhat.qe.storageconsole.helpers.pages.components.MainTabPanel;
import com.redhat.qe.storageconsole.helpers.pages.components.TreeNode;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.mappper.Tag;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

import dstywho.timeout.Timeout;

public class StorageSahiServerTasks {

	/**
	 * 
	 */
	private static final int NUM_ATTEMPTS = 10;
	StorageBrowser storageSahiTasks = null;
	StorageSahiVolumeTasks volumeTasks = null;
	StorageSahiEventMessageTasks storageSahiMessageTasks = null;

	private static String CANNOT_EDIT_HOST_THAT_HAS_VOLUMES = "Cannot edit Host";
	
	public StorageSahiServerTasks(StorageBrowser tasks) {
		storageSahiTasks = tasks;
		volumeTasks = new StorageSahiVolumeTasks(tasks);
		storageSahiMessageTasks = new StorageSahiEventMessageTasks(storageSahiTasks);
	}
	
	/*
	 * Getting server(s) detail as CSV and returns List<Server>
	 */
	public List<Server> getServers(String serverCSV) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		List<Server> servers = new ArrayList<Server>();
		storageSahiTasks._logger.log(Level.INFO, "CSV: "+serverCSV);
		String[] serverList = serverCSV.split(",");
		for(String server : serverList){
			servers.add(TestEnvironmentConfig.getTestEnvironemt().getServer(server.trim()));
		}
		return servers;
	}
	
	//-------------------------------------------------------------------------------------
	// Add/Remove Server
	//-------------------------------------------------------------------------------------

	/*
	 * Adds Server on the specified Cluster 
	 */
	public boolean addServer(ServerMap serverMap){
		storageSahiTasks.selectPage(serverMap.getResourceLocation());
		storageSahiTasks.clickRefresh("Host");
		if(serverMap.isPositive()) {
            if (storageSahiTasks.div(serverMap.getServerName()).exists()) {
                failTest("Server ["+serverMap.getServerName()+"] is already on the list!");
                return false;
            }
		} else {
			if (serverMap.getServerAlreadyOnList()) {
				if (!storageSahiTasks.div(serverMap.getServerName()).exists()) {
					failTest("Pre-Requirement not met: Server ["+serverMap.getServerName()+"] not available on the list!");
					return false;
				}
			} else {
				if (storageSahiTasks.div(serverMap.getServerName()).exists()) {
					failTest("Pre-Requirement not met: Server ["+serverMap.getServerName()+"] already on the list!");
					return false;
				}
			}
		}
		
		storageSahiTasks.div("MainTabHostView_table_New").click();
		storageSahiTasks.select("HostPopupView_cluster").choose(serverMap.getClusterName());
		storageSahiTasks.textbox("HostPopupView_name").setValue(serverMap.getServerName());
		storageSahiTasks.textbox("HostPopupView_host").setValue(serverMap.getServerHostIP());
		storageSahiTasks.password("HostPopupView_userPassword").setValue(serverMap.getServerPassword());
		//storageSahiTasks.div("HostPopupView_OnSaveFalse").click();
		//storageSahiTasks.clickRefresh("Host");
		for(int i = 0; i < 10; i++){
			if(storageSahiTasks.div("OK").exists()){
				storageSahiTasks.div("OK").click();
			}
		}
		if (serverMap.isPositive()) {
            if (!waitForServerUpStatus(serverMap)) {
            	failTest("timed out waiting for sever status to be in Up status");
                return false;
            }
		} else {
		    if(storageSahiTasks.div("/" + serverMap.getErrorMsg() + "/i").exists()) {
		    	storageSahiTasks._logger.log(Level.FINE, "Volume ["+serverMap.getServerName()+"] server error pop-up appeared!");
		    	storageSahiTasks.closePopup("Close");
		    	storageSahiTasks.div("HostPopupView_Cancel").click();
			    return true;
		    } else {
				closeErrorDialogIfExists();
				cancelServerCreateDialogIfExists();
				failTest("Volume ["+serverMap.getServerName()+"] server error pop-up did not appear!");
				return false;
		    }
		}
		
		return true;		
	}

	public void cancelServerCreateDialogIfExists(){
		if(storageSahiTasks.div("HostPopupView_Cancel").isVisible()) storageSahiTasks.div("HostPopupView_Cancel").click();
	}
	
	public void closeErrorDialogIfExists(){
		if(storageSahiTasks.div("/.*Error.*/").isVisible()) storageSahiTasks.closePopup("Close");
	}
	
	public boolean isServerRowExist(ServerMap server){
		ServerTable table = new ServerTable(storageSahiTasks);
		return table.findRow(server) != null;
	}
	
	public boolean waitForServerRowExist(final ServerMap server){
		return WaitUtil.waitUntil(new Predicate<Integer>() {

			@Override
			public boolean apply(Integer attempt) {
				Timeout.SIXTY_SECONDS.sleep();
				return isServerRowExist(server);
			}
		}, NUM_ATTEMPTS);
		
	}

    public boolean waitForServerUpStatus(ServerMap serverMap) {
        HashMap<String, String> row = null;
        row = GuiTables.waitAndGetExpectedResult(storageSahiTasks, GuiTables.TableType.SERVERS, serverMap.getServerName(), GuiTables.STATUS, GuiTables.Status.UP.get(), 1000, 30);
        Assert.assertTrue(row != null, "server row exists in server table");

        if(!row.get(GuiTables.STATUS).equalsIgnoreCase(GuiTables.Status.UP.get())){
                storageSahiTasks._logger.log(Level.WARNING, "Server["+serverMap.getServerName()+"("+serverMap.getServerHostIP()+")] is not "+GuiTables.Status.UP.get()+"!");
            return false;
        }
        if(!storageSahiTasks.div(serverMap.getServerHostIP()).exists()){
                storageSahiTasks._logger.log(Level.WARNING, "Server ["+serverMap.getServerName()+"("+serverMap.getServerHostIP()+")] is not available on the list!");
            return false;
        }
        return true;
    }

	/*
	 * Removes the specified server
	 */
	public boolean removeServer(ServerMap serverMap){
        int wait = 1000;
        int retryCount = 35;

		if(!storageSahiTasks.selectPage(serverMap.getResourceLocation())){
			return false;
		}
        storageSahiTasks.clickRefresh("Host");

		HashMap<String, String> row = moveServerToMaintenanceMode(serverMap);
		if (row == null) {
			return false;
		}
		
		for (int y = 0; y < 3; y++) {
			storageSahiTasks.div(serverMap.getServerHostIP()).click();
			storageSahiTasks.div("MainTabHostView_table_Remove").click();
			storageSahiTasks.div("RemoveConfirmationPopupView_OnRemove").click();
		
			storageSahiTasks.waitFor(5000);
			
			if (storageSahiTasks.div("Cannot remove Host. Related operation is currently in progress").exists()) {
				storageSahiTasks._logger.log(Level.WARNING, "Server["+serverMap.getServerName()+"("+serverMap.getServerHostIP()+")] Error: operation in progress!");
				storageSahiTasks.closePopup("Close");
			} else {
				break;
			}
		}
		
		if(serverMap.getErrorMsg()!=null){//Server having bricks should not be removed.
			if(storageSahiTasks.div(serverMap.getErrorMsg()).exists()){
				storageSahiTasks.closePopup("Close");
				//this.div(serverMap.getServerName()).click();
				storageSahiTasks.div(row.get(GuiTables.NAME)).click();
				storageSahiTasks.div("Activate").click();
				return true;
			}
		}
		storageSahiTasks.clickRefresh("Host");
		
        for (int x = 0; x < retryCount; x++) {
            if(!storageSahiTasks.div(serverMap.getServerHostIP()).exists()) {
                    break;
            } else {
                    storageSahiTasks.wait(wait, retryCount, x);
            }
        }
        
		if(storageSahiTasks.div(serverMap.getServerHostIP()).exists()){
			storageSahiTasks._logger.log(Level.WARNING, "Server["+serverMap.getServerName()+"("+serverMap.getServerHostIP()+")] is available on the list!");
			return false;
		}

		return true;		
	}

	private boolean moveServerToUpMode(ServerMap serverMap) {
		
        String serverStatus = GuiTables.getServer(storageSahiTasks, serverMap.getServerName()).get(GuiTables.STATUS);
        if(!serverStatus.equalsIgnoreCase(GuiTables.Status.UP.get())){
        	storageSahiTasks.div("Activate").click();
        }
        
		HashMap<String, String> row = null;
		row = GuiTables.waitAndGetExpectedResult(storageSahiTasks, GuiTables.TableType.SERVERS, serverMap.getServerName(), GuiTables.STATUS, GuiTables.Status.UP.get(), 1000*5, 10);

		if(!row.get(GuiTables.STATUS).equalsIgnoreCase(GuiTables.Status.UP.get())){
			storageSahiTasks._logger.log(Level.WARNING, "Server["+serverMap.getServerName()+"("+serverMap.getServerHostIP()+")] is not in "+GuiTables.Status.UP.get()+" mode!");
			return false;
		}
		
		return true;
	}
	
	public HashMap<String, String> moveServerToMaintenanceMode(ServerMap serverMap) {
		Assert.assertTrue(new ServerTab(storageSahiTasks).getTable().waitUntilVisible(), "server table failed to display");
		
        String serverStatus = GuiTables.getServer(storageSahiTasks, serverMap.getServerName()).get(GuiTables.STATUS);
        if(!serverStatus.equalsIgnoreCase(GuiTables.Status.MAINTENANCE.get())){
        	storageSahiTasks.div(serverMap.getServerHostIP()).in(new MainTabPanel(storageSahiTasks).getElementStub()).click();
        	storageSahiTasks.div("MainTabHostView_table_Maintenance").in(new MainTabPanel(storageSahiTasks).getElementStub()).click();
        	storageSahiTasks.div("DefaultConfirmationPopupView_OnMaintenance").click();
        }
        
		HashMap<String, String> row = null;
		row = GuiTables.waitAndGetExpectedResult(storageSahiTasks, GuiTables.TableType.SERVERS, serverMap.getServerName(), GuiTables.STATUS, GuiTables.Status.MAINTENANCE.get(), 1000*5, 10);

		if(!row.get(GuiTables.STATUS).equalsIgnoreCase(GuiTables.Status.MAINTENANCE.get())){
			storageSahiTasks._logger.log(Level.WARNING, "Server["+serverMap.getServerName()+"("+serverMap.getServerHostIP()+")] is not in "+GuiTables.Status.MAINTENANCE.get()+" mode!");
			return null;
		}
		
		return row;
	}

	public boolean removeServerNotInMaintenace(ServerMap serverMap) {
		
		if(!storageSahiTasks.selectPage(serverMap.getResourceLocation())){
				return false;
		}
		
		waitForServerTableToLoad();
		
		storageSahiTasks.div(serverMap.getServerHostIP()).click();  // Click on Server

		String serverStatus = GuiTables.getServer(storageSahiTasks, serverMap.getServerName()).get(GuiTables.STATUS);
		if(serverStatus.equalsIgnoreCase(GuiTables.Status.MAINTENANCE.get())){
			storageSahiTasks._logger.log(Level.WARNING, "Server ["+serverMap.getServerName()+"] is already in " + GuiTables.Status.MAINTENANCE.get() + "!");
			return false;
		}
		
		storageSahiTasks.div("MainTabHostView_table_Remove").click();  // Click Remove button
		
	    if(storageSahiTasks.div("/" + "Are you sure you want to remove the following Server(s)?" + "/").exists()) {
	    	storageSahiTasks._logger.log(Level.WARNING, "Server ["+serverMap.getServerName()+"] Remove Server pop-up unexpectedly appeared!");
	    	storageSahiTasks.div("RemoveConfirmationPopupView_Cancel").click();
		    return false;
	    } else {
	    	storageSahiTasks._logger.log(Level.FINE, "Server ["+serverMap.getServerName()+"] Remove Server pop-up did not appear!");
	    }
		
		return true;
	}
	
    public boolean renameServerInUpState(ServerMap server, String newServerName) {
        boolean foundServer = false;

        if(!storageSahiTasks.selectPage(server.getResourceLocation())) {
                return false;
        }
        storageSahiTasks.clickRefresh("Host");
        
        String serverStatus = GuiTables.getServer(storageSahiTasks, server.getServerName()).get(GuiTables.STATUS);
        if(!serverStatus.equalsIgnoreCase(GuiTables.Status.UP.get())){
                storageSahiTasks._logger.log(Level.WARNING, "Server ["+server.getServerName()+"] not " + GuiTables.Status.UP.get() + "!");
                return false;
        }
       
        editServer(server, newServerName, null);

        LinkedList<HashMap<String, String>> serversTable = GuiTables.getServersTable(storageSahiTasks);

        for(HashMap<String, String> row : serversTable) {
                // Find the desired row by server's IP
                if(row.get(GuiTables.HOST_IP).equals(server.getServerHostIP())) {
                        if (!row.get(GuiTables.NAME).equals(newServerName)) {
                                storageSahiTasks._logger.log(Level.WARNING, "Server[" + row.get(GuiTables.NAME) + "] does not contain expected new server name [" + newServerName + "]!");
                                editServer(server, server.getServerName(), null);
                                return false;
                        }
                        foundServer = true;
                }
        }

        if (!foundServer) {
                storageSahiTasks._logger.log(Level.WARNING, "Server[expectedServerName] not found in server table!");
                editServer(server, server.getServerName(), null);
                return false;
        }

        // Return back to original name, as not to impact any subsequent tests that depend on original name.
        editServer(server, server.getServerName(), null);

        return true;
    }
    
    public boolean editServerInMaintenanceState(ServerMap server) {
        String expectedServerName = server.getServerName() + System.currentTimeMillis();
        String expectedClusterName = "Default";
        boolean foundServer = false;

        if(!storageSahiTasks.selectPage(server.getResourceLocation())) {
            return false;
        }
        storageSahiTasks.clickRefresh("Host");

		if (moveServerToMaintenanceMode(server) == null) {
			return false;
		}
		
        String serverStatus = GuiTables.getServer(storageSahiTasks, server.getServerName()).get(GuiTables.STATUS);
        if(!serverStatus.equalsIgnoreCase(GuiTables.Status.MAINTENANCE.get())){
            storageSahiTasks._logger.log(Level.WARNING, "Server ["+server.getServerName()+"] not in " + GuiTables.Status.MAINTENANCE.get() + "!");
            return false;
        }

        editServer(server, expectedServerName, expectedClusterName);

        LinkedList<HashMap<String, String>> serversTable = GuiTables.getServersTable(storageSahiTasks);

        for(HashMap<String, String> row : serversTable) {
            // Find the desired row by server's IP
            if(row.get(GuiTables.HOST_IP).equals(server.getServerHostIP())) {
                if (!row.get(GuiTables.NAME).equals(expectedServerName)) {
                    storageSahiTasks._logger.log(Level.WARNING, "Server[" + row.get(GuiTables.NAME) + "] does not contain expected new server name [" + expectedServerName + "]!");
                    editServer(server, server.getServerName(), null);
                    return false;
                }

                if (!row.get(GuiTables.CLUSTER).equals(expectedClusterName)) {
                    storageSahiTasks._logger.log(Level.WARNING, "Server[" + row.get(GuiTables.NAME) + "] does not contain expected new cluster name [" + expectedClusterName + "]!");
                    editServer(server, server.getServerName(), null);
                    return false;
                }

                foundServer = true;
            }
        }

        if (!foundServer) {
            storageSahiTasks._logger.log(Level.WARNING, "Server[expectedServerName] not found in server table!");
            editServer(server, server.getServerName(), null);
            return false;
        }

        // Return back to original name, as not to impact any subsequent tests that depend on original name.
        editServer(server, server.getServerName(), server.getClusterName());

        return true;
    }
 
    
    public boolean editServerInMaintenanceStateThatHasVolumes(ServerMap server) {
        String expectedClusterName = "Default";
        String expectedServerName = server.getServerName() + System.currentTimeMillis();

        Assert.assertTrue(volumeTasks.doesServerHaveVolumes(server),"Server ["+server.getServerName()+"] has no volumes!");
        
        if(!storageSahiTasks.selectPage(server.getResourceLocation())) {
            return false;
        }
        storageSahiTasks.clickRefresh("Host");

		if (moveServerToMaintenanceMode(server) == null) {
			return false;
		}
		
		for (int i = 0; i < 3; i++) {
			storageSahiTasks.clickRefresh("Host");
		}

        String serverStatus = GuiTables.getServer(storageSahiTasks, server.getServerName()).get(GuiTables.STATUS);
        if(!serverStatus.equalsIgnoreCase(GuiTables.Status.MAINTENANCE.get())){
            storageSahiTasks._logger.log(Level.WARNING, "Server ["+server.getServerName()+"] not in " + GuiTables.Status.MAINTENANCE.get() + "!");
            return false;
        }

        // Validate that Server name is editable
        
        editServer(server, expectedServerName, server.getClusterName());
        if (!storageSahiTasks.div("/" + expectedServerName + "/").exists()) {
            storageSahiTasks._logger.log(Level.WARNING, "Server[" + server.getServerName() + "] name not changed!");
            return false;
        } else {
	        // Return to original name
        	editServer(server, server.getServerName(), server.getClusterName());
        }
        
        // Validate that Cluster is not editable
        
        editServer(server, server.getServerName(), expectedClusterName);

        if (!storageSahiTasks.div("/" + CANNOT_EDIT_HOST_THAT_HAS_VOLUMES + "/").exists()) {
            storageSahiTasks._logger.log(Level.WARNING, "Server[" + server.getServerName() + "] does not contain expected error message!");
            if (storageSahiTasks.div("Close").exists()) {
            	storageSahiTasks.div("Close").click();
            }
        	clickCancelButton();
            return false;
        }

    	storageSahiTasks.div("Close").click();
    	clickCancelButton();
    	
    	// Return server to Up state
    	moveServerToUpMode(server);
    	
        return true;
    }
    
    public void tagServer(ServerMap server, Tag tag) {
    	storageSahiTasks.selectPage(server.getResourceLocation());
        AssignTagDialog dialog = openAssignTagDialog(server);
        dialog.assignTags(tag);
    }

    public void untagServer(ServerMap server, Tag tag) {
    	storageSahiTasks.selectPage(server.getResourceLocation());
    	AssignTagDialog dialog = openAssignTagDialog(server);
    	dialog.getTagList().openAllTags();    	

    	dialog.uncheckItemInTagList(tag);
    	
    	dialog.getOkButton().getElementStub().click();
    	Assert.assertTrue(dialog.waitUntilNotVisible(), "assign tag dialog did close");
    }

	/**
	 * @param server
	 * @return
	 * @throws ExecutionException
	 */
	private AssignTagDialog openAssignTagDialog(ServerMap server)
			throws ExecutionException {
		storageSahiTasks.div(server.getServerHostIP()).click();  // Click on Server
    	storageSahiTasks.div(server.getServerHostIP()).rightClick();  // Click on Server
    	new ContextMenu(storageSahiTasks).getItem("Assign Tags").toElementStub(storageSahiTasks).click();  
    	
    	AssignTagDialog dialog = new AssignTagDialog(storageSahiTasks);
    	dialog.waitUntilVisible();
		return dialog;
	}
    private void editServer(ServerMap server, String expectedServerName, String expectedClusterName) {
        storageSahiTasks.div(server.getServerHostIP()).click();  // Click on Server
        storageSahiTasks.div("MainTabHostView_table_Edit").click();
        storageSahiTasks.textbox("HostPopupView_name").setValue(expectedServerName);
        if (expectedClusterName != null) {
            storageSahiTasks.select("HostPopupView_cluster").choose(expectedClusterName);
        }

        for(int i = 0; i < 5; i++){
            if(storageSahiTasks.div("OK").exists()){
    			storageSahiTasks._logger.log(Level.INFO, "Server ["+server.getServerName()+"] click OK.");
                storageSahiTasks.div("OK").click();
            } else {
                break;
            }
        }
    }
    
    private void clickCancelButton() {
    	
        for(int i = 0; i < 5; i++){
            if(storageSahiTasks.div("Cancel").exists()){
    			storageSahiTasks._logger.log(Level.INFO, "Click Cancel.");
    			storageSahiTasks.div("Cancel").click();
            } else {
                break;
            }
        }
    }
    
    public void waitForServerTableToLoad() {
    	boolean result = WaitUtil.waitUntil(new Predicate<Integer>() {
    		public boolean apply(Integer att){
    			return storageSahiTasks.div("/" + GuiTables.SERVERS_TABLE_REFERENCE + "/").isVisible();
    		}
    	}, 10, "Server Table is visible!");
    	Assert.assertTrue(result, "Server table did not appear!");
    }
}
