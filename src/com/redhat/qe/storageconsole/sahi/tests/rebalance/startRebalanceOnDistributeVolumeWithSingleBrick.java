package com.redhat.qe.storageconsole.sahi.tests.rebalance;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.RebalanceTasks;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiVolumeTasks;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

public class startRebalanceOnDistributeVolumeWithSingleBrick extends SahiTestBase{
	StorageSahiVolumeTasks tasks = null;
	RebalanceTasks rtasks = null;
	
	
	@BeforeMethod
	public void setUp() {
		tasks = new StorageSahiVolumeTasks(browser);
		rtasks = new RebalanceTasks(browser);
	}



	@Tcms("301786")
	@Test (dataProvider="distributeVolumeCreationDataWithSingleBrick")
	public void test(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
				Assert.assertTrue(tasks.createVolume(volumeMap).isSuccessful(), "Volume["+volumeMap.getVolumeName()+"] creation status!");
				Assert.assertTrue(tasks.startVolume(volumeMap), "Volume["+volumeMap.getVolumeName()+"] starting status!");
				rtasks.selectFromContextMenu(volumeMap, "Rebalance");
				Assert.assertTrue(rtasks.getDialog().waitUntilVisible(), "error dialog did not display");
				Assert.assertTrue(rtasks.getDialog().getText().contains("Error while executing action: Cannot rebalance Gluster Volume. Gluster Volume has a single brick.")
						,"dialog does not contain correct error text" );
	}

@DataProvider(name="distributeVolumeCreationDataWithSingleBrick")
public Object[][] getdistributeVolumeCreationDataWithSingleBrick() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
	ArrayList<Object> data = new ArrayList<Object>();
	VolumeMap volumeMap = new VolumeMap();
	String authAllowTestValue = null;
	volumeMap.setResourceLocation("System->Volumes");
	volumeMap.setClusterName("automation_cluster1");
	volumeMap.setVolumeName("automation-volume-distribute-singlebrick");
	volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
	volumeMap.setServers("{server23=bricks-rebalance-singlebrick}");
	volumeMap.setNfsEnabled(true);
	volumeMap.setSpecialCount(0);
	volumeMap.setClientMachines(TestEnvironmentConfig.getTestEnvironemt().getClientMachines());
	volumeMap.setVolumeOptionKey(VolumeMap.optionType.DIAGNOSTICS_BRICK_SYS_LOG_LEVEL.toString());
	volumeMap.setVolumeOptionValue("ERROR");
	volumeMap.setVolumeEditOptionValue("CRITICAL");
	authAllowTestValue = TestEnvironmentConfig.getTestEnvironemt().getGeneralKeyValueMapFromKey("SINGLE_IP").getValue();
	volumeMap.setVolumeAuthAllowValue(authAllowTestValue);
	data.add(volumeMap);
	volumeMap = new VolumeMap();
	return this.convertListTo2dArray(data);
}
	

}