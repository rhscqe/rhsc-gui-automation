/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;

import org.testng.Assert;

import com.google.common.base.Predicate;
import com.redhat.qe.storageconsole.helpers.*;
import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.pages.components.MainTabPanel;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;

/**
 * @author mmahoney 
 * Aug 8, 2013
 */
public class StorageSahiHooksTasks {

	StorageBrowser storageSahiTasks = null;
	private StorageCLITasks cliTasks;
	
	static final String hookName = "qeTestHook" + System.currentTimeMillis() + ".sh";
	
	static final boolean ENABLE_HOOK = true;
	static final boolean DISABLE_HOOK = false;

	static final String HOOK_ENABLED_STATUS = "Enabled";
	static final String HOOK_DISABLED_STATUS = "Disabled";
	static final String RESOLVE_CONFLICTS_DIALOG = "Hook status is inconsistent";
	static final String ENABLED_PREFIX = "S";
	static final String DISABLED_PREFIX = "K";
	
	JQuery tableSelector = new JQuery(".gwt-SplitLayoutPanel .gwt-SplitLayoutPanel .gwt-SplitLayoutPanel div:eq(1)").find("th:contains(Volume Event):eq(1)").addCall("closest", "table");

	public StorageSahiHooksTasks(StorageBrowser tasks) {
		storageSahiTasks = tasks;
		cliTasks = new StorageCLITasks();
	}
	
	public boolean enableHooksSingleServer(ServerMap server) throws CannotStartConnectException, IOException {
		
		// Create test Hook file
		cliTasks.createDisabledHook(server, hookName);
		
		// Enable the Hook
		changeHookStatus(server, hookName, ENABLE_HOOK);
		
		// Delete the Hook
		deleteHook(server, hookName);
		
		return true;
	}
	
	public boolean disableHookSingleServer(ServerMap server) throws CannotStartConnectException, IOException {
		
		// Create test Hook file
		cliTasks.createEnabledHook(server, hookName);
		
		// Disable the Hook
		changeHookStatus(server, hookName, DISABLE_HOOK);
		
		// Delete the Hook
		deleteHook(server, hookName);
		
		return true;
	}
	
	public boolean hooksList(ServerMap server) throws CannotStartConnectException, IOException {
		
		// Get list of Hooks from server
		ArrayList <String> expectedHooksList = cliTasks.getHooksList(server);
		Collections.sort(expectedHooksList);
		
		// Get list of Hooks from UI
		ArrayList <String> actualHooksList = getHooksList(server);
		Collections.sort(actualHooksList);
		
		if (expectedHooksList.size() != actualHooksList.size()) {
			storageSahiTasks._logger.log(Level.INFO, "Hook: Expected (from server) hooks list: ");
			for (String hook : expectedHooksList) {
				storageSahiTasks._logger.log(Level.INFO, "  " + hook + "");
			}
			storageSahiTasks._logger.log(Level.INFO, "Hook: Actual (from UI) hooks list: ");
			for (String hook : actualHooksList) {
				storageSahiTasks._logger.log(Level.INFO, "  " + hook + "");
			}
			Assert.assertTrue(false, "Hooks: Size miss-match - expected-size [" + expectedHooksList.size()  + "]  actual-size [" +  actualHooksList.size() + "]!");
		}
				
		// Compare list
		for (int i = 0; i < expectedHooksList.size(); i++) {
			Assert.assertTrue(expectedHooksList.get(i).equals(actualHooksList.get(i)), "Hooks: Did not match - expected [" + expectedHooksList.get(i) + "  actual [" + actualHooksList.get(i) + "]\n");
		}
		
		return true;
	}
	
	public boolean resolveHookCopyToAllServers(ArrayList <ServerMap> serverMapList) throws IOException {
		ServerMap baseServer = serverMapList.get(0);
		ServerMap secondServer = serverMapList.get(1);
		
		// Create hook
		cliTasks.createEnabledHook(baseServer, hookName);
		navigateToHooksTable(baseServer);
		
		// Resolve hook - Copy to all servers
		copyHookToAllServers();
		waitForHookToCopy(secondServer, hookName);
		
		// Note: Can not delete Hook after it has been copied to all servers
		
		return true;
	}
	
	public boolean resolveConflicts(ArrayList <ServerMap> serverMapList) throws IOException {
		
		ServerMap server1 = serverMapList.get(0);
		ServerMap server2 = serverMapList.get(1);
		
		// Setup
		cliTasks.createEnabledHook(server1, hookName);
		cliTasks.createDisabledHook(server2, hookName);
		
		// Resolve Conflict (Either Enable or Disable)
		navigateToHooksTable(server1);
		resolveConflictsOnServers(DISABLE_HOOK);
		
		// Validate Enabled or Disabled on both servers
		
		// Hook should now be in Disabled status
		HashMap<String, String> hookRow = getHookRow(hookName);
		Assert.assertTrue(hookRow.get(GuiTables.STATUS).equals(HOOK_DISABLED_STATUS), "Hook: Incorrect Hook [" + hookName + "] Status!");
		
		// Hook should now be in same status on both servers
		Assert.assertTrue(cliTasks.doesFileExist(server1, DISABLED_PREFIX + hookName), "Hook: Incorrect Hook status on server [" + server1 + "]");
		Assert.assertTrue(cliTasks.doesFileExist(server2, DISABLED_PREFIX + hookName), "Hook: Incorrect Hook status on server [" + server2 + "]");

		return true;
	}
	
	/*
	 * Helper Methods
	 */
	
	private boolean changeHookStatus(ServerMap server, String hookName, boolean enableHook) {
		
		navigateToHooksTable(server);
		
		// Wait for Hook file to appear
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(storageSahiTasks.div(hookName)), 20), "Hook: Did not appear [" + hookName + "]");
		storageSahiTasks.div(hookName).click();
		
		// Get the Hook row
		HashMap<String, String> hookRow = getHookRow(hookName);
		
		if (enableHook) {
			storageSahiTasks._logger.log(Level.INFO, "Enable Hook[" + hookName + "].");
			
			if (!hookRow.get(GuiTables.STATUS).equals(HOOK_ENABLED_STATUS)) {
				storageSahiTasks.div("Enable").click();
				
				// Wait for Status to change.
				waitForHookStatus(hookName, HOOK_ENABLED_STATUS);
				
			} else {
				Assert.assertTrue(true, "Hook[" + hookName + "] is already Enabled.");
			}
			
			// Validate Hook is enabled on server
			
		} else {
			storageSahiTasks._logger.log(Level.INFO, "Disable Hook[" + hookName + "].");
			
			if (!hookRow.get(GuiTables.STATUS).equals(HOOK_DISABLED_STATUS)) {
				storageSahiTasks.div("Disable").click();
				
				// Confirm Disable - OK
				storageSahiTasks.closePopup("OK");
				
				// Wait for Status to change.
				waitForHookStatus(hookName, HOOK_DISABLED_STATUS);
				
			} else {
				Assert.assertTrue(true, "Hook[" + hookName + "] is already Disabled.");
			}
			
			// Validate Hook is disabled on server
		}
		
		return true;
	}

	/**
	 * @param server
	 */
	private void navigateToHooksTable(ServerMap server) {
		Assert.assertTrue(storageSahiTasks.selectPage("System->Clusters"));
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(storageSahiTasks.div(server.getClusterName())), 10), "Hooks: Cluster [" + server.getClusterName() + "] did not appear!");
		storageSahiTasks.div(server.getClusterName()).near(storageSahiTasks.div("Name")).click();
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(storageSahiTasks.div("Gluster Hooks")), 10), "Hooks: \"Gluster Hooks\" button did not appear!");
		storageSahiTasks.div("Gluster Hooks").click();
		storageSahiTasks.div("Sync").click(); // Ensure that the test file is Synced
	}

	private void deleteHook(ServerMap server, String hookName) throws IOException {
		resolveConflictsButton();
		storageSahiTasks.radio("GlusterHookResolveConflictsPopupView_resolveMissingConflictRemoveEditor").click();
		storageSahiTasks.closePopup("OK");
		
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsNotVisible(storageSahiTasks.div(hookName)), 10), "Hook: Failed to delete Hook [" + hookName + "]");
		waitForHookToBeRemovedFromServer(server, hookName);
	}
	
	private void waitForHookStatus(final String hookName, final String expectedStatus) {
		WaitUtil.waitUntil(new Predicate<Integer>() {
			public boolean apply(Integer attempt) {
				// System.out.printf("Status: " + getHookRow(hookName).get(GuiTables.STATUS) + "\n");
				return getHookRow(hookName).get(GuiTables.STATUS).equals(expectedStatus);
			}
		}, 10);
		
		Assert.assertTrue(getHookRow(hookName).get(GuiTables.STATUS).equals(expectedStatus), "Hooks: Did not find Hook [" + hookName + "] expected status [" + expectedStatus + "]!");
	}
	
	HashMap<String, String> getHookRow(String hookName) {
		ArrayList<HashMap<String, String>> hooksTable = getHooksTable();
		
		return findRowByColumnName(hooksTable, GuiTables.NAME, hookName);
	}
	
	private HashMap<String, String> findRowByColumnName(ArrayList<HashMap<String, String>> table, String columnName, String valueToFind) {
		HashMap<String, String> row = null;
		
		for (HashMap<String, String> hooksRow : table) {
			if(hooksRow.get(columnName).equals(valueToFind)) {
				row = hooksRow;
				break;
			}
		}
		
		Assert.assertTrue(row != null, "Hooks: Unable to find value: " + valueToFind);
		
		return row;
	}
	
	private ArrayList <String> getHooksList(ServerMap server) {
		ArrayList <String> hooksList = new ArrayList<String>();
		
		navigateToHooksTable(server);
		
		for (HashMap<String, String> hooksRow : getHooksTable()) {
			hooksList.add(hooksRow.get(GuiTables.NAME));
		}
		return hooksList;
	}

	private ArrayList<HashMap<String, String>> getHooksTable() {
		TableElement table = new TableElement(tableSelector, SahiTestBase.getStorageSahiTasks());
		ArrayList<HashMap<String, String>> hooksTable = table.getData();
		return hooksTable;
	}
	
	private void copyHookToAllServers() {
		resolveConflictsButton();
		storageSahiTasks.radio("GlusterHookResolveConflictsPopupView_resolveMissingConflictCopyEditor").click();
		storageSahiTasks.closePopup("OK");
	}

	private void waitForHookToCopy(final ServerMap server, final String hookName) throws IOException {
	
		WaitUtil.waitUntil(new Predicate<Integer>() {
			public boolean apply(Integer attempt) {
				try {
					return cliTasks.doesFileExist(server, hookName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		}, 20);
	
		Assert.assertTrue(cliTasks.doesFileExist(server, hookName), "Hook: Failed to copy [" + hookName + "] to server [" + server.getServerHostIP() + "]!");
	}
	
	private void waitForHookToBeRemovedFromServer(final ServerMap server, final String hookName) throws IOException {
		
		WaitUtil.waitUntil(new Predicate<Integer>() {
			public boolean apply(Integer attempt) {
				try {
					return !cliTasks.doesFileExist(server, hookName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		}, 20);
	
		Assert.assertFalse(cliTasks.doesFileExist(server, hookName), "Hook: Failed to remove [" + hookName + "] from server [" + server.getServerHostIP() + "]!");
	}
	
	private void resolveConflictsOnServers(boolean enableHook) {
		resolveConflictsButton();
		storageSahiTasks.checkbox("GlusterHookResolveConflictsPopupView_resolveStatusConflict").check();
		
		if (enableHook) {
			storageSahiTasks.radio("GlusterHookResolveConflictsPopupView_resolveStatusConflictEnable").click();
		} else {
			storageSahiTasks.radio("GlusterHookResolveConflictsPopupView_resolveStatusConflictDisable").click();
		}
		storageSahiTasks.closePopup("OK");
	}
	
	private void resolveConflictsButton() {
		int counter = 20;
		
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(storageSahiTasks.div(hookName)), 10), "Hooks: Hook [" + hookName + "] did not appear");
		storageSahiTasks.div(hookName).click();

		// After clicking on Hook, the Resolve Conflicts button may take some time before becoming active.
		for (int i = 1; i <= counter; i++) {
			storageSahiTasks.div("Resolve Conflicts").click();
			if (storageSahiTasks.div("Conflicts Reasons").exists()) {
				break;
			} else {
				storageSahiTasks._logger.log(Level.INFO,"Hook: Resolve Conflicts button not active - attempt " + i + "/" + counter);
				Duration.ONE_SECOND.sleep();
			}
		}
		Assert.assertTrue(storageSahiTasks.div("Resolve Conflicts").exists(), "Hook: Resolve Conflicts button was not active!");
	}

}
