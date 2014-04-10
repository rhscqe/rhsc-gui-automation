/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Jan 16, 2013
 */
public class NfsMountVolumeOnEditOptionTest extends MountVolumeTestBase{

	@BeforeMethod
	public void setup() throws IOException, TestEnvironmentConfigException, JAXBException {
		super.setup(volumeWithoutNfsEnabled());
	}

	/**
	 * 
	 */
	private void enableNfs() {
		getVolumeMap().setVolumeOptionKey("nfs.disable");
		getVolumeMap().setVolumeEditOptionValue("no");
		Assert.assertTrue(getTasks().editVolumeOption(getVolumeMap()), "could not edit volume option");
	}
	
	@Test
	public void test() throws IOException, JAXBException, TestEnvironmentConfigException{
		super.validateMountingNfsShare(false);
		enableNfs();
		super.validateMountingNfsShare(true);
	}
	
	protected VolumeMap volumeWithoutNfsEnabled() {
		VolumeMap volMap = new VolumeMap();
		volMap.setResourceLocation("System->System->Volumes");
		volMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volMap.setSpecialCount(0);
		volMap.setClusterName("automation_cluster1");
		volMap.setVolumeName("automation-volume-distribute-nfs-tests");
		volMap.setServers("{server23=bricks-distribute-6}{server24=bricks-distribute-6}");
		volMap.setNfsEnabled(false);
		return volMap;
	}

}
