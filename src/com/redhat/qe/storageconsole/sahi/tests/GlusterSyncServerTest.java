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
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.listeners.depend.HaltAllSubsequentOnFailure;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiGlusterSyncServerTasks;
import com.redhat.qe.storageconsole.te.Sshable;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author mmahoney 
 * Jan 28, 2013
 */
public class GlusterSyncServerTest extends SahiTestBase {
	
	StorageSahiGlusterSyncServerTasks tasks = null;

	@BeforeMethod
	public void setUp() {
		tasks = new StorageSahiGlusterSyncServerTasks(browser);
	}
	
	@Test
	@HaltAllSubsequentOnFailure
	public void syncServerAdd() throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException {
		Assert.assertTrue(tasks.syncServerAdd(getServerData()), "Sync gluster server add!");
	}
	
	@Test
	public void syncServerRemove() throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException {
		Assert.assertTrue(tasks.syncServerRemove(getServerData()), "Sync gluster server remove!");
	}
	
	public ArrayList<ServerMap> getServerData() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<ServerMap> data = new ArrayList<ServerMap>();
		
		for(Sshable server : TestEnvironmentConfig.getTestEnvironemt().getServers()) {
			ServerMap serverObj = new ServerMap();
			serverObj.setResourceLocation("Hosts");
			serverObj.setServerName(server.getHostname()); // Sync Added server will be Imported the server, which uses the IP in the Name field
			serverObj.setServerHostIP(server.getHostname());
			serverObj.setServerUsername(server.getLogin());
			serverObj.setServerPassword(server.getPassword());
			serverObj.setClusterName("Default");
			serverObj.setClusterName("automation_cluster1");
			data.add(serverObj);
		}		
		return data;		
	}
}
