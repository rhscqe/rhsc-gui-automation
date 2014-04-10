/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Jan 16, 2013
 */
public class NfsMountVolumeOnCreationTest extends MountVolumeTestBase {

	@BeforeMethod
	public void setup() throws IOException, TestEnvironmentConfigException, JAXBException {
		super.setup(volumeWithNfsEnabledData());
				
	}

	
	VolumeMap volumeWithNfsEnabledData() {
		VolumeMap volMap = new VolumeMap();
		volMap.setResourceLocation("System->System->Volumes");
		volMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volMap.setSpecialCount(0);
		volMap.setClusterName("automation_cluster1");
		volMap.setVolumeName("automation-volume-distribute-nfs-tests");
		volMap.setServers("{server23=bricks-distribute-6}{server24=bricks-distribute-6}");
		volMap.setNfsEnabled(true);
		return volMap;
	}


	@Test
	public void validateMountingNfsShare() throws IOException, JAXBException, TestEnvironmentConfigException{
		boolean isSuccessful = true;
		super.validateMountingNfsShare(isSuccessful);
	}


}
