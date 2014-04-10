/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;

import com.google.common.base.Predicate;
import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.mappper.ServerMap;

/**
 * @author mmahoney 
 * April 8, 2013
 */
public class StorageSahiGlusterSyncServerTasks {
	private final String SERVER_ADD_DETECTED = "Host .* was added by admin@internal.";
	private final String SERVER_REMOVE_DETECTED = "Detected server .*. removed from Cluster .*.";
	
	StorageCLITasks storageCLITasks = new StorageCLITasks();
	StorageSahiEventTasks storageSahiEventTasks = null;
	StorageBrowser storageSahiTasks = null;
	StorageSahiServerTasks storageSahiServerTasks = null;
	StorageSahiImportTasks importTasks = null;
	StorageSahiEventMessageTasks storageSahiMessageTasks = null;
	
	public StorageSahiGlusterSyncServerTasks(StorageBrowser tasks){
		storageSahiTasks = tasks;
		storageSahiEventTasks = new StorageSahiEventTasks(storageSahiTasks);
		storageSahiServerTasks = new StorageSahiServerTasks(storageSahiTasks);
		importTasks = new StorageSahiImportTasks(storageSahiTasks);
		storageSahiMessageTasks = new StorageSahiEventMessageTasks(storageSahiTasks);
	}
	
	public boolean syncServerAdd (ArrayList<ServerMap> serverMapList) throws IOException {
		ServerMap baseServer = serverMapList.get(0);
		ServerMap serverToAdd = serverMapList.get(1);
		
		if(!storageSahiTasks.selectPage(baseServer.getResourceLocation())){
			return false;
		}
        storageSahiTasks.clickRefresh("Host");
        
		// Setup - Add base server to via Console

        storageSahiServerTasks.addServer(baseServer);
		storageSahiServerTasks.waitForServerTableToLoad();

        // Begin test - Add server
        
		String command = storageCLITasks.commandGlusterPeerProbe + serverToAdd.getServerHostIP();
		String commandOutput = storageCLITasks.runGenericCommand(baseServer.getServerHostIP(), baseServer.getServerUsername(), baseServer.getServerPassword(), command);
		Assert.assertTrue(commandOutput.contains("success"), "Server [" + serverToAdd.getServerHostIP() + "] add failed!");
		
		ArrayList<ServerMap> serversToImport = new ArrayList<ServerMap>();
		serversToImport.add(serverToAdd);
		Assert.assertTrue(importTasks.importServers(baseServer.getClusterName(), serversToImport), "Server ["+ serverToAdd.getServerHostIP() + "] did not add!");
		
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(SERVER_ADD_DETECTED));

		// Cleanup - Remove servers
		
		for (ServerMap server : serverMapList) {
			Assert.assertTrue(storageSahiServerTasks.removeServer(server), "Server [" + server.getServerHostIP() + "] failed to remove!");
		}
		
		return true;
	}
	
	public boolean syncServerRemove (ArrayList<ServerMap> serverMapList) throws IOException {
		ServerMap baseServer = serverMapList.get(0);
		ServerMap serverToRemove = serverMapList.get(1);
		int retryCount = 25;
		
		if(!storageSahiTasks.selectPage(baseServer.getResourceLocation())){
			return false;
		}
        storageSahiTasks.clickRefresh("Host");
        
        // Setup - Add server(s) if not already there
        
		for (ServerMap server : serverMapList) {
			// Note: false may be returned if server is already on the list
			storageSahiServerTasks.addServer(server);
		}
        
		storageSahiServerTasks.waitForServerTableToLoad();
		
        LinkedList<HashMap<String, String>> serversTable = GuiTables.getServersTable(storageSahiTasks);
        int numberOfServers = serversTable.size();
        		
        Assert.assertTrue((numberOfServers >= 2), "Not enough servers to continue!");
        
        // Begin test - Remove server
        
		String command = storageCLITasks.commandGlusterPeerDetach + serverToRemove.getServerHostIP();
		String commandOutput = storageCLITasks.runGenericCommand(baseServer.getServerHostIP(), baseServer.getServerUsername(), baseServer.getServerPassword(), command);
		if (!commandOutput.contains("success")) {
			storageSahiTasks._logger.log(Level.WARNING, "Server [" + baseServer.getServerHostIP() + "] detach failed!");
			return false;
		}
		
		// Wait for server to be removed.
		// Note: It is random as to which server will be removed from the console.
		
		for (int x = 0; x < retryCount; x++) {
			serversTable = GuiTables.getServersTable(storageSahiTasks);
			if((serversTable.size()) < numberOfServers) {
				// storageSahiTasks._logger.log(Level.INFO, "Server [" + baseServer.getServerHostIP() + "] detach failed!");
				break;
			} else {
				if(x == (retryCount -1)) {
					Assert.assertTrue(false, "Server Remove timeout.");;
				}
				storageSahiTasks.wait(500, retryCount, x);
				storageSahiTasks.clickRefresh("Host");
			}
		}
		
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(SERVER_REMOVE_DETECTED));

		// Cleanup - Remove remaining server.
		
		storageSahiTasks._logger.log(Level.INFO, "Begin cleanup!");
		
		ServerMap server = null;
		String ip = serversTable.get(0).get(GuiTables.HOST_IP);
		if (ip.equals(baseServer.getServerHostIP())) {
			server = baseServer;
		} else {
			server = serverToRemove;
		}
		
		if (!storageSahiServerTasks.removeServer(server)) {
			storageSahiTasks._logger.log(Level.WARNING, "Server [" + server.getServerHostIP() + "] failed to remove!");
        	return false;
		}
		
		return true;
	}
	
}
