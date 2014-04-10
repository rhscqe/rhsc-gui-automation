/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import net.sf.sahi.command.Log;

import org.joda.time.Duration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.storageconsole.helpers.CannotStartConnectException;
import com.redhat.qe.storageconsole.helpers.RhscCleanerTool;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.cli.Ssher;
import com.redhat.qe.storageconsole.helpers.elements.Row;
import com.redhat.qe.storageconsole.helpers.elements.ServerTable;
import com.redhat.qe.storageconsole.helpers.elements.tab.ServerTab;
import com.redhat.qe.storageconsole.helpers.fixtures.RepositoryContainer;
import com.redhat.qe.storageconsole.helpers.fixtures.RestFixtureTestBase;
import com.redhat.qe.storageconsole.helpers.fixtures.ShellConfiguration;
import com.redhat.qe.storageconsole.helpers.fixtures.ShellSession;
import com.redhat.qe.storageconsole.helpers.fixtures.VolumeFixtureHelper;
import com.redhat.qe.storageconsole.helpers.fixtures.VolumeMapFactory;
import com.redhat.qe.storageconsole.helpers.pages.components.FooterEventTable;
import com.redhat.qe.storageconsole.helpers.pages.components.MainTabPanel;
import com.redhat.qe.storageconsole.helpers.ssh.SshResult;
import com.redhat.qe.storageconsole.mappper.ClusterMap;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.sahi.base.SSHClient;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiServerTasks;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

import dstywho.timeout.Timeout;

/**
 * @author dustin 
 * Aug 27, 2013
 */
public class HostStatusWhenHostIsDownTest  extends RestFixtureTestBase{
	/**
	 * 
	 */
	private static final int TEN = 10;
	final protected static Logger LOG = Logger.getLogger(HostStatusWhenHostIsDownTest.class.getName());
	/**
	 * 
	 */
	private static final int NUM_ATTEMPTS = 20;
	private VolumeMap volume;
	private ClusterMap cluster;
	
	
	/* (non-Javadoc)
	 * @see com.redhat.qe.storageconsole.sahi.tests.server.CliTestBase#setupUsingCli(com.redhat.qe.storageconsole.helpers.fixtures.RepositoryContainer)
	 */
	@Override
	public void setupData(RepositoryContainer repos) throws TestEnvironmentConfigException {
		cluster = ClusterMap.clusterMap("hoststatuscluster");
		volume = new VolumeMapFactory().distributedVolume("hoststatusVol", cluster.getClusterName());
		new VolumeFixtureHelper().create(repos, volume);
		
	}
	

	@AfterMethod
	public void after() throws CannotStartConnectException, TestEnvironmentConfigException{
		
		new Ssher().runCommand(new Function<SSHClient, SshResult>() {

			@Override
			public SshResult apply(SSHClient client) {
				return client.startVdsm();
			}
		}, volume.getConfiguredServers().get(0));

	}
	
	@Test
	@Tcms("169955")
	public void test() throws CannotStartConnectException, TestEnvironmentConfigException{
		final Server server = volume.getConfiguredServers().get(0);
	
		restartServer(server);
		stopVdsm(server);

		navigateToServerPage(server);

		Assert.assertTrue( waitUntilServerStatusisConnecting(server), "wait for server status to be Connecting");
		Assert.assertTrue(waitUntilServerStatusIsNotRepsonsiveOrNonResponsiveIsInEventTable(server), "wait for server status to be Non Responsive");
		new StorageSahiServerTasks(browser).moveServerToMaintenanceMode(getServerMap(server));
	}

	/**
	 * @param server
	 * @return
	 */
	private boolean waitUntilServerStatusisConnecting(final Server server) {
		return WaitUtil.waitUntil(new Predicate<Integer>() {
			
			@Override
			public boolean apply(Integer attempt) {
				new MainTabPanel(browser).clickRefresh();
				LOG.log(Level.INFO, "waiting for status connecting--current status is " + getServerStatus(server));
				return getServerStatus(server).toLowerCase().contains("connecting");
			}

		}, NUM_ATTEMPTS);
	}
	
	private boolean waitUntilServerStatusIsNotRepsonsiveOrNonResponsiveIsInEventTable(final Server server) {
		return waitUntilServerStatusIsNotResponsive(server) || isEventTableContainMessage(server, "non responsive");
	}

	/**
	 * @param server
	 * @return
	 */
	private boolean isEventTableContainMessage(Server server, String message) {
		ArrayList<Row> lastTenMessages = new FooterEventTable(getBrowser()).getRowsWithIndexLessThan(TEN);
		for(Row row: lastTenMessages){
			if(row.getText().toLowerCase().contains(message) && row.getText().contains(server.getName()))
				return true;
		}
		return false;
	}

	/**
	 * @param server
	 * @return
	 */
	private boolean waitUntilServerStatusIsNotResponsive(final Server server) {
		return WaitUtil.waitUntil(new Predicate<Integer>() {
			
			@Override
			public boolean apply(Integer attempt) {
				new MainTabPanel(browser).clickRefresh();
				LOG.log(Level.INFO, "waiting for status 'non responsive'--current status is " + getServerStatus(server));
				return getServerStatus(server).toLowerCase().contains("non responsive");
			}
		}, NUM_ATTEMPTS, new Timeout(Duration.millis(500)));
	}

	/**
	 * @param server
	 * @throws CannotStartConnectException
	 */
	private void stopVdsm(final Server server) throws CannotStartConnectException {
		Timeout.TIMEOUT_TEN_SECONDS.sleep(); //wait for vdsmd to start
		
		waitForVdsmdToStart(server);
		new Ssher().runCommand(new Function<SSHClient, SshResult>() {

			@Override
			public SshResult apply(SSHClient client) {
				return client.stopVdsm();
			}
		}, server);
	}

	/**
	 * @param server
	 */
	private void waitForVdsmdToStart(final Server server) {
		Assert.assertTrue(WaitUtil.waitUntil(new Predicate<Integer>() {
			
			@Override
			public boolean apply(Integer attempt) {
				try {
					return vdsmStatus(server).isSuccessful();
				} catch (CannotStartConnectException e) {
					throw new RuntimeException(e);
				}
			}
		}, NUM_ATTEMPTS), "wait for vdsm service to start");
	}

	/**
	 * @param server
	 * @return
	 * @throws CannotStartConnectException
	 */
	private SshResult vdsmStatus(Server server)
			throws CannotStartConnectException {
		return new Ssher().runCommand(new Function<SSHClient, SshResult>() {
			
			@Override
			public SshResult apply(SSHClient client) {
				return client.vdsmStatus();
			}
		}, server);
	}

	/**
	 * @param server
	 * @throws CannotStartConnectException
	 */
	private void restartServer(Server server)
			throws CannotStartConnectException {
		new Ssher().runCommand(new Function<SSHClient, Boolean>() {
			
			@Override
			public Boolean apply(SSHClient client) {
				return client.restart();
			}
		}, server);
		Assert.assertTrue(new Ssher().waitUntilSeverStopsResponding(server), "waiting for server to stop responding");
		Assert.assertTrue(new Ssher().waitUntilSeverStarts(server), "waiting for server to start responding");
	}

	/**
	 * @param server
	 */
	private void navigateToServerPage(Server server) {
		browser.selectPage(getServerMap(server).getResourceLocation());
		ServerTable serverTable = new ServerTable(browser);
		serverTable.waitUntilVisible();
	}

	/**
	 * @param server
	 * @param serverTable
	 * @return 
	 */
	private String getServerStatus(Server server) {
		ServerTable serverTable = new ServerTable(browser);
		Row serverRow = serverTable.getFirstRowThatContainsText(server.getName());
		return serverRow.getCell(serverTable.getHeaders().indexOf("Status")).getText();
	}

	/**
	 * @param server
	 * @return 
	 */
	private ServerMap getServerMap(Server server) {
		return ServerMap.fromServer(server, cluster.getClusterName());
	}


}
