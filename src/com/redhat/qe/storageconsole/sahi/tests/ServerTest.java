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

import com.redhat.qe.storageconsole.listeners.depend.HaltAllSubsequentOnFailure;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiEventMessageTasks;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiServerTasks;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 10, 2012
 */
public class ServerTest extends SahiTestBase{
	private final String EVENT_MSG_SERVER_ADDED = "State was set to Up for host .*..";
	private final String EVENT_MSG_SERVER_REMOVED = "Host .*. was removed by";
	private final String EVENT_MSG_SERVER_RENAMED = "Host server23 was renamed from .*. to ";
	private final String EVENT_MSG_SERVER_MOVED_TO_MAINTENANCE = "Host .*. was switched to Maintenance mode by";
	private final String EVENT_MSG_SERVER_EDITED_PARAMETERS_UPDATED_BY = "Host .*. configuration was updated by admin@internal";
	private final String EVENT_MSG_SERVER_EDITED_RENAMED = "Host .*. was renamed from .*. to ";
	
	StorageSahiServerTasks tasks = null;
	StorageSahiEventMessageTasks storageSahiMessageTasks = null;

	
	@BeforeMethod
	public void setUp() {
		tasks = new StorageSahiServerTasks(browser);
		storageSahiMessageTasks = new StorageSahiEventMessageTasks(browser);
	}
	
	@Test(dataProvider="serverCreationData")
	@HaltAllSubsequentOnFailure
	public void addServer(ServerMap serverMap){
		Assert.assertTrue(tasks.addServer(serverMap), "Server["+serverMap.getServerHostIP()+"] Addition status");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_SERVER_ADDED.replace(".*.", serverMap.getServerName())));
	}
	
	@Test(dataProvider="serverCreationData")
	public void removeServer(ServerMap serverMap){
		Assert.assertTrue(tasks.removeServer(serverMap), "Server["+serverMap.getServerHostIP()+"] Addition status");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_SERVER_MOVED_TO_MAINTENANCE.replace(".*.", serverMap.getServerName())));
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_SERVER_REMOVED.replace(".*.", serverMap.getServerName())));
	}
	
	@Test(dataProvider="removeServerNotInMaintenaceData")
	public void removeServerNotInMaintenaceNegative(ServerMap serverMap){
		Assert.assertTrue(tasks.removeServerNotInMaintenace(serverMap), "Server["+serverMap.getServerHostIP()+"] remove not in maintenance.");
	}
	
	@Test(dataProvider="negativeCasesData")
	public void removeServerNegative(ServerMap serverMap){
		Assert.assertTrue(tasks.removeServer(serverMap), "Server["+serverMap.getServerHostIP()+"] Addition status");
	}
	
	@Test(dataProvider="serverAlreadyExistsData")
	public void addServerAlreadyExistsNegative(ServerMap serverMap){
		Assert.assertTrue(tasks.addServer(serverMap), "Server["+serverMap.getServerHostIP()+"] aready exists!");
	}
	
	@Test(dataProvider="invalidPasswordData")
	public void addServerWithInvalidPassword(ServerMap serverMap){
		Assert.assertTrue(tasks.addServer(serverMap), "Server["+serverMap.getServerHostIP()+"] added!");
	}
	
    @Test
    public void renameServerInUpState() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
        ServerMap server = (ServerMap) getServerCreationgData()[0][0];
        String newServerName = server.getServerName() + System.currentTimeMillis();
        Assert.assertTrue(tasks.renameServerInUpState(server), "Server[" + server.getServerHostIP() + "] rename server in up state!");
        Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_SERVER_EDITED_RENAMED.replace(".*.", newServerName) + server.getServerName() + "."));
        Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_SERVER_EDITED_PARAMETERS_UPDATED_BY.replace(".*.", server.getServerName())));
    }

    @Test
    public void editServerInMaintenance() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
        ServerMap server = (ServerMap) getServerCreationgData()[0][0];
        String newServerName = server.getServerName() + System.currentTimeMillis();
        Assert.assertTrue(tasks.editServerInMaintenanceState(server), "Server[" + server.getServerHostIP() + "] edit server in maintenance state!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_SERVER_RENAMED.replace(".*.", server.getServerName())));
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_SERVER_EDITED_RENAMED.replace(".*.", newServerName) + server.getServerName() + "."));
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_SERVER_EDITED_PARAMETERS_UPDATED_BY.replace(".*.", server.getServerName())));
    }

    @Test
    public void editServerInMaintenanceThatHasVolumes() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
        ServerMap server = (ServerMap) getServerCreationgData()[0][0];
        Assert.assertTrue(tasks.editServerInMaintenanceStateThatHasVolumes(server), "Server[" + server.getServerHostIP() + "] edit server in maintenance state!");
    }
    
	@DataProvider(name="serverCreationData")
	public Object[][] getServerCreationgData() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<Object> data = new ArrayList<Object>();
		
		for(Server server : TestEnvironmentConfig.getTestEnvironemt().getServers()){
			data.add(ServerMap.fromServer(server, "automation_cluster1"));
		}		
		return this.convertListTo2dArray(data);		
	}
	
	@DataProvider(name="negativeCasesData")
	public Object[][] removeServerNegativeData() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<Object> data = new ArrayList<Object>();
		ServerMap serverObj = new ServerMap();
		Server server = TestEnvironmentConfig.getTestEnvironemt().getServers().get(0);
		
		serverObj.setPositive(false);
		serverObj.setErrorMsg("/Cannot remove Host. Server having Gluster volume/");
		serverObj.setResourceLocation("Hosts");
		serverObj.setServerName(server.getName());
		serverObj.setServerHostIP(server.getHostname());
		serverObj.setServerPassword(server.getPassword());
		serverObj.setClusterName("automation_cluster1");
		data.add(serverObj);
				
		return this.convertListTo2dArray(data);		
	}
	
	
	@DataProvider(name="serverAlreadyExistsData")
	public Object[][] addServerNameAlreadyExists() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<Object> data = new ArrayList<Object>();
		ServerMap serverObj = new ServerMap();
		Server server = TestEnvironmentConfig.getTestEnvironemt().getServers().get(0);
		
		serverObj.setPositive(false);
		serverObj.setResourceLocation("Hosts");
		serverObj.setServerName(server.getName());
		serverObj.setServerHostIP(server.getHostname());
		serverObj.setServerPassword(server.getPassword());
		serverObj.setClusterName("automation_cluster1");
		serverObj.setErrorMsg("Cannot add host");
		data.add(serverObj);
				
		return this.convertListTo2dArray(data);		
	}
	
	@DataProvider(name="removeServerNotInMaintenaceData")
	public Object[][] removeServerNotInMaintenace() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<Object> data = new ArrayList<Object>();
		ServerMap serverObj = new ServerMap();
		Server server = TestEnvironmentConfig.getTestEnvironemt().getServers().get(0);
		
		serverObj.setResourceLocation("Hosts");
		serverObj.setServerName(server.getName());
		serverObj.setServerHostIP(server.getHostname());
		serverObj.setServerPassword(server.getPassword());
		serverObj.setClusterName("automation_cluster1");
		data.add(serverObj);
				
		return this.convertListTo2dArray(data);		
	}
	
	@DataProvider(name="invalidPasswordData")
	public Object[][] invalidPasswor() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<Object> data = new ArrayList<Object>();
		ServerMap serverObj = new ServerMap();
		Server server = TestEnvironmentConfig.getTestEnvironemt().getServers().get(0);
		
		serverObj.setPositive(false);
		serverObj.setServerAlreadyOnList(false);
		serverObj.setResourceLocation("System->Hosts");
		serverObj.setServerName(server.getName());
		serverObj.setServerHostIP(server.getHostname());
		serverObj.setServerPassword("invalidPassWord");
		serverObj.setClusterName("automation_cluster1");
		serverObj.setErrorMsg("verify authentication parameters are correct");
		data.add(serverObj);
				
		return this.convertListTo2dArray(data);		
	}
}
