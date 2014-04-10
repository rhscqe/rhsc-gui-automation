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

import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.storageconsole.helpers.CannotStartConnectException;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiEventMessageTasks;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiHooksTasks;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author mmahoney 
 * Aug 8, 2013
 */
public class HooksTest extends SahiTestBase {

	StorageSahiHooksTasks tasks = null;
	StorageSahiEventMessageTasks eventTasks = null;
	
	@BeforeMethod
	public void setUp() {
		tasks = new StorageSahiHooksTasks(browser);
		eventTasks = new StorageSahiEventMessageTasks(browser);
	}
	
	@Tcms("259389")
	@Test
	public void enableHookSingleServer() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException, CannotStartConnectException {
		ServerMap server = getServerData().get(0);
		Assert.assertTrue(tasks.enableHooksSingleServer(server), "Server["+server.getServerHostIP()+"] Enable Hook!");
	}
	
	@Tcms("259393")
	@Test
	public void disableHookSingleServer() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException, CannotStartConnectException {
		ServerMap server = getServerData().get(0);
		Assert.assertTrue(tasks.disableHookSingleServer(server), "Server["+server.getServerHostIP()+"] Disable Hook!");
	}

	@Tcms("260564")
	@Test
	public void hooksList() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException, CannotStartConnectException {
		ServerMap server = getServerData().get(0);
		Assert.assertTrue(tasks.hooksList(server), "Server["+server.getServerHostIP()+"] Hooks List!");
	}
	
	@Tcms("259395")
	@Test
	public void resolveHookCopyToAllServers() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException, CannotStartConnectException {
		Assert.assertTrue(tasks.resolveHookCopyToAllServers(getServerData()), "Server Resolve Conflicts - Copy To All Servers!");
	}
	
	@Tcms({"273505","273514"})
	@Test
	public void resolveConflicts() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException, CannotStartConnectException {
		Assert.assertTrue(tasks.resolveConflicts(getServerData()), "Server Resolve Conflicts!");
	}
	
	@DataProvider(name="serverData")
	private ArrayList<ServerMap> getServerData() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<ServerMap> data = new ArrayList<ServerMap>();
		
		for(Server server : TestEnvironmentConfig.getTestEnvironemt().getServers()){
			data.add(ServerMap.fromServer(server, "automation_cluster1"));
		}		
		return data;		
	}
}
