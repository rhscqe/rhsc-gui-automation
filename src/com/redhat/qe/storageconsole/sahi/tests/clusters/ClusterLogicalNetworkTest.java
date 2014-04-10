/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.clusters;

import org.junit.After;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.storageconsole.helpers.RhscCleanerTool;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.elements.ContextMenu;
import com.redhat.qe.storageconsole.helpers.elements.Dialog;
import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.fixtures.RepositoryContainer;
import com.redhat.qe.storageconsole.helpers.fixtures.RestFixtureTestBase;
import com.redhat.qe.storageconsole.helpers.fixtures.ServerFixtureHelper;
import com.redhat.qe.storageconsole.helpers.fixtures.ShellConfiguration;
import com.redhat.qe.storageconsole.helpers.fixtures.ShellSession;
import com.redhat.qe.storageconsole.helpers.pages.components.FooterEventsPanel;
import com.redhat.qe.storageconsole.helpers.pages.components.MainTabPanel;
import com.redhat.qe.storageconsole.helpers.pages.components.SubtabPanel;
import com.redhat.qe.storageconsole.mappper.ClusterMap;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Aug 26, 2013
 */
public class ClusterLogicalNetworkTest extends RestFixtureTestBase{
	
	/**
	 * 
	 */
	private static final String BRIDGE_NAME = "ovirtmgmt";
	private ClusterMap cluster;


	public void setupData(RepositoryContainer repos) throws TestEnvironmentConfigException{
		cluster = ClusterMap.clusterMap("clusterTestLogicalNetwork");
		Server serverConfig = TestEnvironmentConfig.getTestEnvironment().getServers().iterator().next();
		ServerMap server = ServerMap.fromServer(serverConfig, cluster.getClusterName());
		new ServerFixtureHelper().createServer(repos, server );
		
	}
	
	@After
	public void cleanup(){
		RhscCleanerTool.cleanup();
	}
	
	@Tcms("246376")
	@Test
	public void testNewLogical(){
		navigateToLogicalNetworkTab();

		browser.div(BRIDGE_NAME).rightClick();
		new ContextMenu(browser).getItem("Add Network").toElementStub(getBrowser()).click();
		
		Dialog dialog = new Dialog("New Logical Network", browser);
		Assert.assertTrue(dialog.waitUntilVisible());
	}

	@Tcms("246376")
	@Test
	public void testAssignNetworks(){
		navigateToLogicalNetworkTab();

		browser.div(BRIDGE_NAME).rightClick();
		new ContextMenu(browser).getItem("Manage Networks").toElementStub(getBrowser()).click();;
		
		Dialog dialog = new Dialog("Manage Networks", browser);
		Assert.assertTrue(dialog.waitUntilVisible());
	}
	
	@Tcms("246376")
	@Test
	public void testSetAsDisplay(){
		final String expectedEventMessage = String.format("Update Display Network (ovirtmgmt) for Cluster %s. (User: admin@internal)", cluster.getClusterName());
		navigateToLogicalNetworkTab();

		browser.div(BRIDGE_NAME).rightClick();
		new ContextMenu(browser).getItem("Set as Display").toElementStub(getBrowser()).click();		
		boolean isSuccessful = waitForFirstFourRowsContainsEventMessage(expectedEventMessage);
		Assert.assertTrue(isSuccessful, "event did not display correctly");
		
	}

	/**
	 * @param expectedEventMessage
	 * @return
	 */
	private boolean waitForFirstFourRowsContainsEventMessage(
			final String expectedEventMessage) {
		final TableElement eventsTable = new FooterEventsPanel(browser).getTable();
		boolean isSuccessful = WaitUtil.waitUntil(new Predicate<Integer>(){

			@Override
			public boolean apply(Integer paramT) {
				return eventsTable.getJqueryObject().find("tr:lt(4)").addCall("text").fetch(browser).contains(expectedEventMessage);
				// checks that the first 4 rows text contains expected
			}
			
		}, 20);
		return isSuccessful;
	}
	
	
	private void navigateToLogicalNetworkTab() {
		browser.selectPage(cluster.getResourceLocation());
		browser.div(cluster.getClusterName()).in(new MainTabPanel(browser).getElementStub()).click();
		browser.div("Logical Networks").in(new SubtabPanel(browser).getElementStub()).click();
		new SubtabPanel(browser).clickOnTab("Logical Networks");
	}

}
