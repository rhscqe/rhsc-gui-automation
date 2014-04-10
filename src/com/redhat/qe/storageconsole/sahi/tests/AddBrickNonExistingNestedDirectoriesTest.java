/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap.EachBrickAction;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageCLITasks;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiVolumeTasks;
import com.redhat.qe.storageconsole.te.Brick;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Jan 23, 2013
 */
public class AddBrickNonExistingNestedDirectoriesTest extends SahiTestBase{

	private StorageSahiVolumeTasks volumeTasks;
	private VolumeMap volumeMap;
    private static String DELETE_ROOT = "DELETE_ME";

	@BeforeMethod
	public void setup(){
		volumeTasks = new StorageSahiVolumeTasks(browser);
		volumeMap = getVolumeData();
	}
	
	/**
	 * Test does not clean up data
	 */
	@Test(enabled=false)
	public void addBricksExistingVolumeWithNonExistantBrickDirectory() throws IOException, TestEnvironmentConfigException, JAXBException{
		volumeMap.forEachBrick(new DeleteBrickDirectory());
		Assert.assertTrue(volumeTasks.addBricksExistingVolume(volumeMap), "Brick addition to volume["+volumeMap.getVolumeName()+"] creation status!");
	}

	/**
	 * @return
	 */
	private VolumeMap getVolumeData() {
		VolumeMap volumeMap = new VolumeMap();
		volumeMap.setResourceLocation("Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distribute");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-distribute-7}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		return volumeMap;
	}
	
	
	private static class  DeleteBrickDirectory implements EachBrickAction {
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				StorageCLITasks cliTasks = new StorageCLITasks();
				try{
					cliTasks.initConnection(server.getHostname(), server.getLogin(), server.getPassword());
					cliTasks.deleteFilesDirs(brick.getLocation().replaceAll(DELETE_ROOT + ".*", ""));
					
				}catch(Exception e){
					throw new RuntimeException(e);
				}finally{
					cliTasks.closeConnection();
				}
			}
		
	}
	
	
	
	
}
;
