/**
 *
 */
package com.redhat.qe.storageconsole.sahi.tests;

import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.mappper.VolumeOptionsMap;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiGlusterSyncTasks;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author shruti
 * Dec 24, 2012
 */
public class GlusterSyncTest extends SahiTestBase{
	StorageSahiGlusterSyncTasks tasks = null;

	@BeforeMethod
	public void setUp(){
		tasks = new StorageSahiGlusterSyncTasks(browser);
	}
	//Tests sync status with GlusterFS for volume creation from GlusterCLI
	@Test (dataProvider="volumeCreationData")
	public void syncVolumeCreate(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		Assert.assertTrue(tasks.syncVolumeCreate(volumeMap), "Volume ["+volumeMap.getVolumeName()+"] sync status for volume creation from glusterCLI!");
	}
	//Tests sync status with GlusterFS for volume deletion from GlusterCLI
	@Test (dataProvider="volumeCreationData")
	public void syncVolumeDelete(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		Assert.assertTrue(tasks.syncVolumeDelete(volumeMap), "Volume ["+volumeMap.getVolumeName()+" sync status for volume deletion from glusterCLI!");
	}
	//Tests sync status with GlusterFS for volume started from GlusterCLI
	@Test (dataProvider="volumeCreationData")
	public void syncVolumeStart(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		Assert.assertTrue(tasks.syncVolumeStart(volumeMap), "Volume ["+volumeMap.getVolumeName()+" sync status for volume start from glusterCLI!");
	}
	//Tests sync status with GlusterFS for volume stopped from GlusterCLI
	@Test (dataProvider="volumeCreationData")
	public void syncVolumeStop(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		Assert.assertTrue(tasks.syncVolumeStop(volumeMap), "Volume ["+volumeMap.getVolumeName()+" sync status for volume stop from glusterCLI!");
	}
	//Tests sync status with GlusterFS for volume option added from GlusterCLI
	@Test (dataProvider="volumeCreationData")
	public void syncVolumeOptionAdd(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		Assert.assertTrue(tasks.syncVolumeOptionAdd(volumeMap), "Volume ["+volumeMap.getVolumeName()+" sync status for volume option added!");
	}
	//Tests sync status with GlusterFS for volume option edited from GlusterCLI
	@Test (dataProvider="volumeCreationData")
	public void syncVolumeOptionEdit(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		Assert.assertTrue(tasks.syncVolumeOptionEdit(volumeMap), "Volume ["+volumeMap.getVolumeName()+" sync status for volume option edited!");
	}
	//Tests sync status with GlusterFS for volume option reset from GlusterCLI
	@Test (dataProvider="volumeCreationData")
	public void syncVolumeOptionReset(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		Assert.assertTrue(tasks.syncVolumeOptionReset(volumeMap), "Volume ["+volumeMap.getVolumeName()+" sync status for volume option edited!");
	}
	//Tests sync status with GlusterFS for all volume options reset from GlusterCLI
	@Test (dataProvider="volumeCreationData")
	public void syncVolumeOptionResetAll(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		//To make sure that there are more than one volume options set on the volume before running reset-all command
		List<VolumeOptionsMap> volumeOptions =  volumeMap.getVolumeOptions();
		VolumeOptionsMap option = new VolumeOptionsMap(VolumeMap.optionType.AUTH_ALLOW.toString(), "*", null);
		volumeOptions.add(option);
		option = new VolumeOptionsMap(VolumeMap.optionType.PERFORMANCE_FLUSH_BEHIND.toString(), "on", null);
		volumeOptions.add(option);
		volumeMap.setVolumeOptions(volumeOptions);
		Assert.assertTrue(tasks.syncVolumeOptionAdd(volumeMap), "Volume ["+volumeMap.getVolumeName()+" sync status for volume option added!");
		//now run reset-all options
		Assert.assertTrue(tasks.syncVolumeOptionResetAll(volumeMap), "Volume ["+volumeMap.getVolumeName()+" sync status for volume option edited!");
	}
	@DataProvider(name="volumeCreationData")
	public Object[][] getVolumeCreationData() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<Object> data = new ArrayList<Object>();
		VolumeMap volumeMap = new VolumeMap();
		//Volume of distribute type
		volumeMap.setResourceLocation("System->System->Volumes");
		volumeMap.setVolumeName("sync-volume-distribute");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-distribute-1}{server24=bricks-distribute-1}");
		volumeMap.setSpecialCount(0);
		List<VolumeOptionsMap> volumeOptions = new ArrayList<VolumeOptionsMap>();
		VolumeOptionsMap option = new VolumeOptionsMap(VolumeMap.optionType.DIAGNOSTICS_BRICK_SYS_LOG_LEVEL.toString(), "ERROR", "CRITICAL");
		volumeOptions.add(option);
		volumeMap.setVolumeOptions(volumeOptions);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		//Volume of replicate type
		volumeMap.setResourceLocation("System->System->Volumes");
		volumeMap.setVolumeName("sync-volume-replicate");
		volumeMap.setVolumeType(VolumeMap.VolumeType.REPLICATE.toString());
		volumeMap.setServers("{server23=bricks-replicate-1}{server24=bricks-replicate-1}");
		volumeMap.setSpecialCount(8);
		volumeOptions = new ArrayList<VolumeOptionsMap>();
		option = new VolumeOptionsMap(VolumeMap.optionType.DIAGNOSTICS_BRICK_SYS_LOG_LEVEL.toString(), "ERROR", "CRITICAL");
		volumeOptions.add(option);
		volumeMap.setVolumeOptions(volumeOptions);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		//Volume of striped type
		volumeMap.setResourceLocation("System->System->Volumes");
		volumeMap.setVolumeName("sync-volume-stripe");
		volumeMap.setVolumeType(VolumeMap.VolumeType.STRIPE.toString());
		volumeMap.setServers("{server23=bricks-stripe-1}{server24=bricks-stripe-1}");
		volumeMap.setSpecialCount(8);
		volumeOptions = new ArrayList<VolumeOptionsMap>();
		option = new VolumeOptionsMap(VolumeMap.optionType.DIAGNOSTICS_BRICK_SYS_LOG_LEVEL.toString(), "ERROR", "CRITICAL");
		volumeOptions.add(option);
		volumeMap.setVolumeOptions(volumeOptions);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		//Volume of distributed-replicate type
		volumeMap.setResourceLocation("System->System->Volumes");
		volumeMap.setVolumeName("sync-volume-distributed-replicate");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTED_REPLICATE.toString());
		volumeMap.setServers("{server23=bricks-distributed-replicate-1}{server24=bricks-distributed-replicate-1}");
		volumeMap.setSpecialCount(4);
		volumeOptions = new ArrayList<VolumeOptionsMap>();
		option = new VolumeOptionsMap(VolumeMap.optionType.DIAGNOSTICS_BRICK_SYS_LOG_LEVEL.toString(), "ERROR", "CRITICAL");volumeOptions.add(option);
		volumeMap.setVolumeOptions(volumeOptions);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		//Volume of distributed-striped type
		volumeMap.setResourceLocation("System->System->Volumes");
		volumeMap.setVolumeName("sync-volume-distributed-stripe");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTED_STRIPE.toString());
		volumeMap.setServers("{server23=bricks-distributed-stripe-1}{server24=bricks-distributed-stripe-1}");
		volumeMap.setSpecialCount(4);
		volumeOptions = new ArrayList<VolumeOptionsMap>();
		option = new VolumeOptionsMap(VolumeMap.optionType.DIAGNOSTICS_BRICK_SYS_LOG_LEVEL.toString(), "ERROR", "CRITICAL");
		volumeOptions.add(option);
		volumeMap.setVolumeOptions(volumeOptions);
		data.add(volumeMap);
		return this.convertListTo2dArray(data);
	}

}
