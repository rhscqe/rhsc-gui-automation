/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.testng.Assert;

import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.elements.tab.ClusterTab;
import com.redhat.qe.storageconsole.helpers.elements.tab.UserTab;

/**
 * @author mmahoney 
 * Apr 15, 2013
 */
public class StorageSahiSearchTasks {
	/**
	 * 
	 */


	StorageBrowser storageSahiTasks = null;
	
	private final String searchBox = "SearchPanelView_searchStringInput";
	private final String searchButton = "SearchPanelView_searchButton";
	private final String clearButton = "SearchPanelView_clearButton";
	
	private final String equalTo = "=";
	private final String notEqualTo = "!=";
	
	private final String clusterNameAttribute = "cluster : name ";
	private final String clusterDescriptionAttribute = "cluster: description ";
	
	private final String serverNameAttribute = "host : name ";
	private final String serverStatusAttribute = "host : status ";

	private final String volumeNameAttribute = "volume : name ";
	private final String volumeTypeAttribute = "volume : type ";

	private final String userNameAttribute = "user : name ";
	
	public StorageSahiSearchTasks(StorageBrowser tasks) {
		storageSahiTasks = tasks;
	}
	
	// Cluster tests
	
	public boolean searchByClusterName() {
		searchByClusterAttribute(clusterNameAttribute + equalTo, GuiTables.NAME);
		searchByClusterAttribute(clusterNameAttribute + notEqualTo, GuiTables.NAME);
		return true;
	}
	
	public boolean searchByClusterDescription() {
		searchByClusterAttribute(clusterDescriptionAttribute + equalTo, GuiTables.DESCRIPTION);
		searchByClusterAttribute(clusterDescriptionAttribute + notEqualTo, GuiTables.DESCRIPTION);
		return true;
	}
	
	// Host / Server tests
	
	public boolean searchByHostName() {
		searchByHostAttribute(serverNameAttribute + equalTo, GuiTables.NAME);
		searchByHostAttribute(serverNameAttribute + notEqualTo, GuiTables.NAME);
		return true;
	}
	
	public boolean searchByHostStatus() {
		searchByHostAttribute(serverStatusAttribute + equalTo, GuiTables.STATUS);
		searchByHostAttribute(serverStatusAttribute + notEqualTo, GuiTables.STATUS);
		return true;
	}
	
	// Volume tests
	
	public boolean searchByVolumeName() {
		searchByVolumeAttribute(volumeNameAttribute + equalTo, GuiTables.NAME);
		searchByVolumeAttribute(volumeNameAttribute + notEqualTo, GuiTables.NAME);
		return true;
	}
	
	public boolean searchByVolumeType() {
		searchByVolumeAttribute(volumeTypeAttribute + equalTo, GuiTables.VOLUME_TYPE);
		searchByVolumeAttribute(volumeTypeAttribute + notEqualTo, GuiTables.VOLUME_TYPE);
		return true;
	}
	
	// User tests
	public boolean searchByUserName() {
		searchByUserAttribute(userNameAttribute + equalTo, GuiTables.USER_FIRST_NAME);
		searchByUserAttribute(userNameAttribute + notEqualTo, GuiTables.USER_FIRST_NAME);
		return true;
	}
	
	/*
	 * Helpers
	 */
	
	public boolean searchByClusterAttribute(String searchAttribute, String columnName) {
        Assert.assertTrue(storageSahiTasks.selectPage("Clusters"));
        storageSahiTasks.clickRefresh("Cluster");
        
        Assert.assertTrue(new ClusterTab(storageSahiTasks).waitUntilArrived(), "Cluster table tdid not appear!");
        
		// Get list of clusters
		ArrayList<HashMap<String, String>> clusterTable = new ClusterTab(storageSahiTasks).getTable().getData();;
		Assert.assertTrue((clusterTable.size() > 0), "No clusters available for search!");

		// Get random Cluster row with which to search - randomize the row so that it is not always the same
		HashMap<String, String> expectedRow = clusterTable.get(new Random().nextInt(clusterTable.size()));

		// Do search
		String expectedValue = expectedRow.get(columnName);
		doSearch(searchAttribute + formatSearchString(expectedValue));
		
		// Validate result
		clusterTable =  new ClusterTab(storageSahiTasks).getTable().getData();
		validateSearchResultsTable(clusterTable, columnName, expectedValue, searchAttribute);
		
		return true;
	}
	
	public void searchByHostAttribute(String searchAttribute, String columnName) {
		Assert.assertTrue(storageSahiTasks.selectPage("Hosts"));
		for (int i = 0; i < 3; i++) {
			storageSahiTasks.clickRefresh("Host");
		}

		// Get list of servers
		LinkedList<HashMap<String, String>> serversTable =  GuiTables.getServersTable(storageSahiTasks);
		Assert.assertTrue((serversTable.size() > 0), "No servers available for search!");
		
		// Get random server row with which to search - randomize the row so that it is not always the same
		HashMap<String, String> expectedRow = serversTable.get(new Random().nextInt(serversTable.size()));
		
		// Do search
		String expectedValue = expectedRow.get(columnName);
		doSearch(searchAttribute + formatSearchString(expectedValue));
	
		// Validate result
		serversTable =  GuiTables.getServersTable(storageSahiTasks);
		validateSearchResultsTable(serversTable, columnName, expectedValue, searchAttribute);
	}
	
	public void searchByVolumeAttribute(String searchAttribute, String columnName) {
		Assert.assertTrue(storageSahiTasks.selectPage("Volumes"));
        storageSahiTasks.clickRefresh("Volume");

		// Get list of volumes
		LinkedList<HashMap<String, String>> volumeTable =  GuiTables.getVolumesTable(storageSahiTasks);
		Assert.assertTrue((volumeTable.size() > 0), "No volumes available for search!");
		
		// Get random volume row with which to search - randomize the row so that it is not always the same
		HashMap<String, String> expectedRow = volumeTable.get(new Random().nextInt(volumeTable.size()));
	
		// Do search
		String expectedValue = expectedRow.get(columnName);
		doSearch(searchAttribute + formatSearchString(expectedValue));
	
		// Validate result
		volumeTable =  GuiTables.getVolumesTable(storageSahiTasks);
		validateSearchResultsTable(volumeTable, columnName, expectedValue, searchAttribute);
	}
	
	public void searchByUserAttribute(String searchAttribute, String columnName) {
		Assert.assertTrue(storageSahiTasks.selectPage("Users"));
        storageSahiTasks.clickRefresh("User");

		// Get list of users
		List<HashMap<String, String>> userTable =  new UserTab(storageSahiTasks).getTable().getData();
		Assert.assertTrue((userTable.size() > 0), "No users available for search!");
		
		// Get random volume row with which to search - randomize the row so that it is not always the same
		HashMap<String, String> expectedRow = userTable.get(new Random().nextInt(userTable.size()));
	
		// Do search
		String expectedValue = expectedRow.get(columnName);
		doSearch(searchAttribute + formatSearchString(expectedValue));
	
		// Validate result
		userTable =  new UserTab(storageSahiTasks).getTable().getData();
		validateSearchResultsTable(userTable, columnName, expectedValue, searchAttribute);
	}
	
	private String formatSearchString(String value) {
		return (value.contains(" ") ? String.format("\"%s\"", value) : value);
	}
	
	private void validateSearchResultsTable(List<HashMap<String, String>> table, String columnName, String expectedValue, String searchAttribute) {
		storageSahiTasks.image(clearButton).click();
		
		for (HashMap<String, String> row : table) {
			String actualValue = row.get(columnName);
			if (searchAttribute.contains(notEqualTo)) {
				Assert.assertTrue(!expectedValue.equals(actualValue), "Unexpected search [!=] value [" + actualValue + "]!");
			} else {
				Assert.assertTrue(expectedValue.equals(actualValue), "Unexpected search [=] value [" + actualValue + "]!");
			}
		}
	}
	
	private void doSearch(String searchString) {
		storageSahiTasks._logger.log(Level.INFO, String.format("search string [%s].", searchString));
		storageSahiTasks.textbox(searchBox).setValue(searchString);
		Assert.assertEquals(storageSahiTasks.textbox(searchBox).getValue(),searchString,"search query text field contents");
		storageSahiTasks.image(searchButton).click();
	}
}
