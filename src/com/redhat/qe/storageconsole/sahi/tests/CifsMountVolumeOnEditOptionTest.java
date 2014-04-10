/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Jan 16, 2013
 */
public class CifsMountVolumeOnEditOptionTest extends CifsMountVolumeOnCreationTest{

	@BeforeMethod
	@Override
	public void setup() throws IOException, TestEnvironmentConfigException, JAXBException{
		super.setup(volumeWithoutCifsEnabled());
		getVolumeMap().setVolumeOptionKey("user.cifs");
		getVolumeMap().setVolumeEditOptionValue("on");
		Assert.assertTrue(getTasks().editVolumeOption(getVolumeMap()), "could not edit volume option");
	}
	
	protected VolumeMap volumeWithoutCifsEnabled() {
		VolumeMap volMap = super.volumeWithCifsEnabledData();
		volMap.setCifsEnabled(false);
		return volMap;
	}

}
