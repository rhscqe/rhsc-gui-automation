/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.storageconsole.helpers.Closure;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap.WarningMessage;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiEventMessageTasks;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiVolumeTasks;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiVolumeTasks.TaskResult;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 10, 2012
 */
public class VolumeTest extends SahiTestBase {
	private final String EVENT_MSG_VOLUME_CREATEED = "Gluster Volume .*. created";
	private final String EVENT_MSG_VOLUME_STARTED = "Gluster Volume .*. started";
	private final String EVENT_MSG_VOLUME_STOPPED = "Gluster Volume .*. stopped";
	private final String EVENT_MSG_VOLUME_REMOVED = "Gluster Volume .*. deleted";
	private final String EVENT_MSG_VOLUME_BRICKS_REMOVED = "Bricks removed from Gluster Volume .*.";
	private final String EVENT_MSG_VOLUME_ADD_OPTION = "Volume Option .*. set on ";
	private final String EVENT_MSG_VOLUME_EDIT_OPTION = "Volume Option .*. set on ";
	private final String EVENT_MSG_VOLUME_OPTION_CHANGED = "changed to .*. from";
	private final String EVENT_MSG_VOLUME_RESET_ALL_OPTIONS = "All Volume Options reset on .*.";
	private final String EVENT_MSG_VOLUME_RESET_OPTION = "Volume Option .*.=.*. reset on .*.";
	private final String EVENT_MSG_VOLUME_BRICKS_ADDED = ".*brick\\(s\\) added to volume .*.";
	
	StorageSahiVolumeTasks tasks = null;
	StorageSahiEventMessageTasks storageSahiMessageTasks = null;
	
	@BeforeMethod
	public void setUp() {
		tasks = new StorageSahiVolumeTasks(browser);
		storageSahiMessageTasks = new StorageSahiEventMessageTasks(browser);
	}
	
	@Test (dataProvider="volumeCreationData")
	// Creates a volume
	public void createVolume(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		Assert.assertTrue(tasks.createVolume(volumeMap).isSuccessful(), "Volume["+volumeMap.getVolumeName()+"] creation status!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_VOLUME_CREATEED.replace(".*.", volumeMap.getVolumeName())));
	}
	
	@Test (dataProvider="volumeCreationDataNegative")
	// Negative cases for create volume
	public void createVolumeNegative(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		Assert.assertTrue(tasks.createVolume(volumeMap).equals(TaskResult.EXPECTED_ERROR_RECIEVED), "Volume["+volumeMap.getVolumeName()+"] creation status!");
	}
	
	@Test
	public void addVolumeToEmptyClusterNegative(){
		VolumeMap volumeMap = getVolumeForEmptyClusterData();
		Assert.assertTrue(tasks.addVolumeToEmptyClusterPromptsError(volumeMap), "Error message not recieved during creation of Volume[" + volumeMap.getVolumeName() + "]");
	}
	
	@Test (dataProvider="volumeCreationWithAccessProtocols")
	// Creates volumes with different access protocols
	public void createVolumeWithAccessProtocols(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		Assert.assertTrue(tasks.createVolume(volumeMap).isSuccessful(), "Volume["+volumeMap.getVolumeName()+"] creation status!");
		removeVolume(volumeMap);
	}
	
	@Test (dataProvider="brickAdditionDataNegativeHostDown")
	// Create volume with bricks residing in a host that is down - negative
	public void addBricksNegativeHostDown(VolumeMap volumeMap) throws Exception{
		Assert.assertTrue(tasks.addBrickHostDown(volumeMap), "Volume["+volumeMap.getVolumeName()+"] creation status!");
	}
	
	@Test (dataProvider="brickAdditionData")
	// Adds bricks to existing volume
	public void addBricksExistingVolume(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		Assert.assertTrue(tasks.addBricksExistingVolume(volumeMap), "Brick addition to volume["+volumeMap.getVolumeName()+"] creation status!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(volumeMap.getBricks().size()+EVENT_MSG_VOLUME_BRICKS_ADDED.replace(".*.", volumeMap.getVolumeName())));
	}
	
	@Test (dataProvider="volumeCreationData")
	// Removes a volume in "UP" state - negative
	public void removeVolumeNotStopped(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		Assert.assertTrue(tasks.removeVolumeNotStopped(volumeMap), "Volume["+volumeMap.getVolumeName()+"] deletion status!");
	}
	
	@Test (dataProvider="volumeCreationData")
	// Removes a volume
	public void removeVolume(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		Assert.assertTrue(tasks.removeVolume(volumeMap), "Volume["+volumeMap.getVolumeName()+"] deletion status!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_VOLUME_REMOVED.replace(".*.", volumeMap.getVolumeName())));
	}
	
	@Tcms("171817")
	@Test (dataProvider="brickAdditionDataNegative")
	// Creates a volume with bricks existing in host but not part of any volume - negative case 
	public void createVolumeExistingBricks(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		Assert.assertTrue(tasks.createVolume(volumeMap).isSuccessful(), "Volume["+volumeMap.getVolumeName()+"] deletion status!");
	}

	@Test (enabled=false, dataProvider="volumeDataForDistributedStripeWithSameBrickAndStripeCount")
	// Create distributed stripe volume with same brick and stripe count
	public void createDistributedStripeVolumeWithSameBrickAndStripeCount(VolumeMap volumeMap) throws Exception{
		TaskResult createVolumeResult = TaskResult.ERROR_STOP;
		try{
			createVolumeResult = tasks.createVolume(volumeMap);
		}finally{
			if(tasks.isVolumeExists(volumeMap)){ removeVolume(volumeMap); }
		}
		Assert.assertTrue(createVolumeResult.isSuccessful(), "Volume["+volumeMap.getVolumeName()+"] creation status with appropriate warning to user!");
	}

	@Test (enabled=false, dataProvider="volumeDataForDistributedReplicaWithAllBricksOnOneServer")
	// Create distributed replicated volume with all bricks on one server
	public void createDistributedReplicatedVolumeWithAllBricksSingleServer(VolumeMap volumeMap) throws Exception{
		TaskResult createVolumeResult = TaskResult.ERROR_STOP;
		try{
			createVolumeResult = tasks.createVolume(volumeMap);
		}finally{
			if(tasks.isVolumeExists(volumeMap)){ removeVolume(volumeMap); }
		}
		Assert.assertTrue(createVolumeResult.isSuccessful(), "Volume["+volumeMap.getVolumeName()+"] failed to create distrbuted replica volume with all bricks on a single node and/or failed to warn user");
	}
	
	@Test (enabled=false)
	// Create distributed replicated volume with all bricks on one server
	public void createReplicateVolumeWithAllBricksSingleServer() throws Exception{
		boolean isVolumeCreatedWithWarning = false;
		VolumeMap volumeMap = getVolumeDataForReplicateWithAllBricksOnOneServer();
		try{
			isVolumeCreatedWithWarning = tasks.createVolume(volumeMap).isSuccessful();
		}finally{
			if(tasks.isVolumeExists(volumeMap)){ removeVolume(volumeMap); }
		}
		Assert.assertTrue(isVolumeCreatedWithWarning, "Volume["+volumeMap.getVolumeName()+"] failed to create replicate volume with all bricks on a single node and/or failed to warn user");
	}

	@Test (dataProvider="volumeCreationData")
	// Starts a volume
	public void startVolume(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		Assert.assertTrue(tasks.startVolume(volumeMap), "Volume["+volumeMap.getVolumeName()+"] starting status!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_VOLUME_STARTED.replace(".*.", volumeMap.getVolumeName())));
	}
	
	@Test (dataProvider="volumeCreationData")
	// Tests the access list of a volume
	public void testAuthAllow(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException {
		Assert.assertTrue(tasks.testAuthAllow(volumeMap), "Testing auth.allow for Volume["+volumeMap.getVolumeName()+"]!");
		
	}
	
	@Test (dataProvider="volumeCreationData")
	// Add Volume Options
	public void addVolumeOption(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		Assert.assertTrue(tasks.addVolumeOption(volumeMap), "Volume["+volumeMap.getVolumeName()+"] Add Volume Option!");
		String expectedMessage = EVENT_MSG_VOLUME_ADD_OPTION.replace(".*.",volumeMap.getVolumeOptionKey() + "=" + volumeMap.getVolumeOptionValue());
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(expectedMessage + volumeMap.getVolumeName()));
	}
	
	@Tcms({"169950"})
	@Test (dataProvider="volumeCreationData")
	// Edit Volume Options
	public void editVolumeOption(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException {
		Assert.assertTrue(tasks.editVolumeOption(volumeMap), "Volume["+volumeMap.getVolumeName()+"] Edit Volume Option!");
		String expectedMessage = EVENT_MSG_VOLUME_EDIT_OPTION.replace(".*.",volumeMap.getVolumeOptionKey() + "=" + volumeMap.getVolumeOptionValue());
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(expectedMessage + volumeMap.getVolumeName()));
		expectedMessage = EVENT_MSG_VOLUME_OPTION_CHANGED.replace(".*.",volumeMap.getVolumeEditOptionValue());
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(expectedMessage));
	}
	
	@Test (dataProvider="volumeCreationData")
	// Check whether all permissible values of auth.allow option are accepted
	public void checkAuthAllowValues(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException {
		// Set option key to auth.allow
		volumeMap.setVolumeOptionKey(VolumeMap.optionType.AUTH_ALLOW.toString());
		// Set volumeEditOptionValue to the value of auth.allow to be tested
		volumeMap.setVolumeEditOptionValue(volumeMap.getVolumeAuthAllowValue());
		Assert.assertTrue(tasks.editVolumeOption(volumeMap), "Volume["+volumeMap.getVolumeName()+"] Edit Volume Option!");
	}
	
	@Test (dataProvider="volumeCreationData")
	// Reset Volume Options
	public void resetVolumeOption(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException {
		Assert.assertTrue(tasks.resetVolumeOption(volumeMap), "Volume["+volumeMap.getVolumeName()+"] Reset Volume Option!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_VOLUME_RESET_OPTION));
	}
	
	@Test (dataProvider="volumeCreationData")
	// Reset All Volume Options
	public void resetAllVolumeOptions(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException {
		Assert.assertTrue(tasks.resetAllVolumeOptions(volumeMap), "Volume["+volumeMap.getVolumeName()+"] Reset All Volume Option!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_VOLUME_RESET_ALL_OPTIONS.replace(".*.", volumeMap.getVolumeName())));
	}
	
	@Test (dataProvider="volumeCreationData")
	// Stops a volume
	public void stopVolume(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		Assert.assertTrue(tasks.stopVolume(volumeMap), "Volume["+volumeMap.getVolumeName()+"] stopping status!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_VOLUME_STOPPED.replace(".*.", volumeMap.getVolumeName())));
	}
	
	@Test 
	// Remove Bricks
	public void removeBricks() throws IOException, TestEnvironmentConfigException, JAXBException {
		String volumeName = "automation-volume-distribute";
		VolumeMap volumeMap = getVolumeMapByVolumeName(volumeName);
		
		Assert.assertNotNull(volumeMap, "Volume["+volumeName+"] not found!");
		Assert.assertTrue(tasks.removeBricks(volumeMap), "Volume["+volumeMap.getVolumeName()+"] remove bricks!");
		Assert.assertTrue(storageSahiMessageTasks.validateLogMessage(EVENT_MSG_VOLUME_BRICKS_REMOVED.replace(".*.", volumeMap.getVolumeName())));
	}

	@Test
	// Validate Volume Summary Tab Data
	public void validateVolumeGeneralTab() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		VolumeMap volumeMap = (VolumeMap) getVolumeCreationData()[0][0];
		Assert.assertTrue(tasks.validateVolumeGeneralTab(volumeMap), "Volume["+volumeMap.getVolumeName()+"] General tab data not correct!");
	}
	
	@Test( dataProvider="volumeRejectData" )
	public void rejectOptionTest(String rejectData) throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException, TimeoutException, InterruptedException{
		VolumeMap volume = (VolumeMap) getVolumeCreationData()[0][0];
		volume.setVolumeOptionKey(VolumeMap.optionType.AUTH_REJECT.toString());
		volume.setVolumeOptionValue(rejectData);
		tasks.addAndRemoveVolumeOption(volume);
	}

	@Test( dataProvider="volumeRejectNegativeData" )
	public void negativeVolumeRejectOptionTest(String rejectData) throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		VolumeMap volume = (VolumeMap) getVolumeCreationData()[0][0];
		volume.setVolumeOptionKey(VolumeMap.optionType.AUTH_REJECT.toString());
		volume.setVolumeOptionValue(rejectData);
		Assert.assertFalse(tasks.addVolumeOption(volume), String.format("was able to add reject option for %s using %s", volume.getVolumeName(), rejectData));
	}

	@Test
	// Validate Optimize For Virt Store
	public void optimizeForVirtStore() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		VolumeMap volumeMap = (VolumeMap) getVolumeCreationData()[0][0];
		Assert.assertTrue(tasks.validateOptimizeForVirtStore(volumeMap), "Volume["+volumeMap.getVolumeName()+"] Optimize For Virt Store failed!");
	}
	
	@DataProvider(name="volumeCreationData")
	public Object[][] getVolumeCreationData() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<Object> data = new ArrayList<Object>();
		VolumeMap volumeMap = new VolumeMap();
		String authAllowTestValue = null;
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distribute");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-distribute-1}{server24=bricks-distribute-1}");
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

		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-stripe");
		volumeMap.setVolumeType(VolumeMap.VolumeType.STRIPE.toString());
		volumeMap.setServers("{server23=bricks-stripe-1}{server24=bricks-stripe-1}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(8);
		volumeMap.setClientMachines(TestEnvironmentConfig.getTestEnvironemt().getClientMachines());
		volumeMap.setVolumeOptionKey(VolumeMap.optionType.DIAGNOSTICS_CLIENT_SYS_LOG_LEVEL.toString());
		volumeMap.setVolumeOptionValue("ERROR");
		volumeMap.setVolumeEditOptionValue("CRITICAL");
		authAllowTestValue = TestEnvironmentConfig.getTestEnvironemt().getGeneralKeyValueMapFromKey("COMMA_SEPARATED_IP").getValue();
		volumeMap.setVolumeAuthAllowValue(authAllowTestValue);
		volumeMap.setVolumeIsTechPreview(true);
		data.add(volumeMap);
		volumeMap = new VolumeMap();			
		
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distributed-stripe");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTED_STRIPE.toString());
		volumeMap.setServers("{server23=bricks-distributed-stripe-1}{server24=bricks-distributed-stripe-1}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(4);
		volumeMap.setClientMachines(TestEnvironmentConfig.getTestEnvironemt().getClientMachines());
		volumeMap.setVolumeOptionKey(VolumeMap.optionType.PERFORMANCE_CACHE_MAX_FILE_SIZE.toString());
		volumeMap.setVolumeOptionValue("10240");
		volumeMap.setVolumeEditOptionValue("20480");
		authAllowTestValue = TestEnvironmentConfig.getTestEnvironemt().getGeneralKeyValueMapFromKey("SINGLE_HOSTNAME").getValue();
		volumeMap.setVolumeAuthAllowValue(authAllowTestValue);
		volumeMap.setVolumeIsTechPreview(true);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-replicate");
		volumeMap.setVolumeType(VolumeMap.VolumeType.REPLICATE.toString());
		volumeMap.setServers("{server23=bricks-replicate-1}{server24=bricks-replicate-1}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(8);
		volumeMap.setClientMachines(TestEnvironmentConfig.getTestEnvironemt().getClientMachines());
		volumeMap.setVolumeOptionKey(VolumeMap.optionType.PERFORMANCE_FLUSH_BEHIND.toString());
		volumeMap.setVolumeOptionValue("on");
		volumeMap.setVolumeEditOptionValue("off");
		authAllowTestValue = TestEnvironmentConfig.getTestEnvironemt().getGeneralKeyValueMapFromKey("ALLOW_ALL").getValue();
		volumeMap.setVolumeAuthAllowValue(authAllowTestValue);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distributed-replicate");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTED_REPLICATE.toString());
		volumeMap.setServers("{server23=bricks-distributed-replicate-1}{server24=bricks-distributed-replicate-1}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(4);
		volumeMap.setClientMachines(TestEnvironmentConfig.getTestEnvironemt().getClientMachines());
		volumeMap.setVolumeOptionKey(VolumeMap.optionType.PERFORMANCE_FLUSH_BEHIND.toString());
		volumeMap.setVolumeOptionValue("off");
		volumeMap.setVolumeEditOptionValue("on");
		authAllowTestValue = TestEnvironmentConfig.getTestEnvironemt().getGeneralKeyValueMapFromKey("COMMA_SEPARATED_HOSTNAMES").getValue();
		volumeMap.setVolumeAuthAllowValue(authAllowTestValue);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		
		volumeMap.setResourceLocation("System->Volumes");// Adding bricks from different directories
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distribute-2");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-distribute-3}{server24=bricks-distribute-3}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		volumeMap.setClientMachines(TestEnvironmentConfig.getTestEnvironemt().getClientMachines());
		volumeMap.setVolumeOptionKey(VolumeMap.optionType.PERFORMANCE_LOW_PRIO_THREADS.toString());
		volumeMap.setVolumeOptionValue("10");
		volumeMap.setVolumeEditOptionValue("15");
		authAllowTestValue = TestEnvironmentConfig.getTestEnvironemt().getGeneralKeyValueMapFromKey("COMMA_SEPARATED_IP_HOSTNAME").getValue();
		volumeMap.setVolumeAuthAllowValue(authAllowTestValue);
		data.add(volumeMap);
			
		return this.convertListTo2dArray(data);
	}
	
	@DataProvider(name="volumeDataForDistributedStripeWithSameBrickvolumeMap.setVolumeIsTechPreview(true);AndStripeCount")
	public Object[][] getVolumeDataForDistributedStripeWithSameBrickAndStripeCount() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		final VolumeMap volumeMap = new VolumeMap();			
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distributed-stripe-equal-brick-stripe-count");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTED_STRIPE.toString());
		volumeMap.setServers("{server24=bricks-distributed-stripe-2}");
		volumeMap.setSpecialCount(4);
		volumeMap.setWarningMsg(WarningMessage.VOLUME_TYPE_CHANGING_FROM_DISTRIBUTED_STRIPE_TO_STRIPE);
		return this.convertListTo2dArray(new ArrayList<Object>(){{ add(volumeMap);}});
	}

	@DataProvider(name="volumeDataForDistributedReplicaWithAllBricksOnOneServer")
	public Object[][] getVolumeDataForDistributedReplicaWithAllBricksOnOneServer(){
		final VolumeMap volumeMap = new VolumeMap();
		volumeMap.setPositive(true);
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distributed-replicate-bricks-single-server");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTED_REPLICATE.toString());
		volumeMap.setServers("{server24=bricks-distributed-replicate-2}");
		volumeMap.setSpecialCount(2);
		volumeMap.setWarningMsg(WarningMessage.PROTECT_AGAINST_DISK_FAILURE_MOVE_BRICKS);
		return this.convertListTo2dArray(new ArrayList<Object>(){{ add(volumeMap);}});
	}
	
	public VolumeMap getVolumeDataForReplicateWithAllBricksOnOneServer(){
		final VolumeMap volumeMap = new VolumeMap();
		volumeMap.setPositive(true);
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-replicate-bricks-single-server");
		volumeMap.setVolumeType(VolumeMap.VolumeType.REPLICATE.toString());
		volumeMap.setServers("{server24=bricks-distributed-replicate-2}");
		volumeMap.setSpecialCount(2);
		volumeMap.setWarningMsg(WarningMessage.PROTECT_AGAINST_DISK_FAILURE_MOVE_BRICKS);
		return volumeMap;
	}

	@DataProvider(name="volumeCreationDataNegative")
	public Object[][] getNegativeVolumeData(){
		ArrayList<Object> data = new ArrayList<Object>();
		VolumeMap volumeMap = new VolumeMap();
		// Add invalid brick directory
		volumeMap.setPositive(false);
		volumeMap.setErrorMsg("Brick Directory should start with '/'");
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-invalid-brick-directory");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-test-add-bricks-invalid-directory}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
//		 Add invalid brick directory
		volumeMap.setPositive(false);
		volumeMap.setErrorMsg("Brick Directory should start with '/'");
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-invalid-brick-directory");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server24=bricks-test-add-bricks-invalid-directory}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		// Create volume with name that already exists - negative
		volumeMap.setPositive(false);
		volumeMap.setErrorMsg(StorageSahiVolumeTasks.DUPLICATE_VOLUME_NAME);
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distribute");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-test-duplicate-volume-name}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		// Create a volume of type 'Stripe' with number of bricks unequal to stripe count - negative
		volumeMap.setPositive(false);
		volumeMap.setErrorMsg("Number of bricks should be equal to Stripe Count");
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-stripe-negative");
		volumeMap.setVolumeType(VolumeMap.VolumeType.STRIPE.toString());
		volumeMap.setServers("{server23=bricks-test-incorrect-brick-number}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(4);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		// Create a volume of type 'Distributed Stripe' with number of bricks not a multiple of stripe count - negative
		volumeMap.setPositive(false);
		volumeMap.setErrorMsg("Number of bricks should be a mutiple of Stripe Count");
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distributed-stripe-negative");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTED_STRIPE.toString());
		volumeMap.setServers("{server23=bricks-test-incorrect-brick-number}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(4);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		// Create a volume of type 'Replicate' with number of bricks not equal to replica count- negative
		volumeMap.setPositive(false);
		volumeMap.setErrorMsg("Number of bricks should be equal to Replica Count");
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-replicate-negative");
		volumeMap.setVolumeType(VolumeMap.VolumeType.REPLICATE.toString());
		volumeMap.setServers("{server23=bricks-test-incorrect-brick-number}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(4);
		data.add(volumeMap);
		volumeMap = new VolumeMap();
		// Create a volume of type 'Distributed Replicate' with number of bricks not a multiple of replica count - negative
		volumeMap.setPositive(false);
		volumeMap.setErrorMsg("Number of bricks should be a mutiple of Replica Count");
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distributed-replicate-negative");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTED_REPLICATE.toString());
		volumeMap.setServers("{server23=bricks-test-incorrect-brick-number}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(4);
		data.add(volumeMap);
		// Brick directory already used
		volumeMap = new VolumeMap();
		volumeMap.setPositive(false);
		volumeMap.setDeleteDir(false);
		volumeMap.setErrorMsg("already used by the volume");
		volumeMap.setResourceLocation("Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("brick-dir-already-exists");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-distribute-1}{server24=bricks-distribute-1}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		data.add(volumeMap);
		
		return this.convertListTo2dArray(data);
	}

	@Test (dataProvider="volumeCreationData")
	// Validate Volume Brick Summary Tab Data
	public void validateVolumeBrickSummaryTab(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException, InterruptedException{
	        Assert.assertTrue(tasks.validateBrickSummaryTab(volumeMap), "Volume["+volumeMap.getVolumeName()+"] brick tab failed to be verified!");
	}

	@DataProvider(name="brickAdditionData")
	public Object[][] getBrickAdditionData(){
		ArrayList<Object> data = new ArrayList<Object>();
		VolumeMap volumeMap = new VolumeMap();
		
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distribute");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-distribute-2}{server24=bricks-distribute-2}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		data.add(volumeMap);

		return this.convertListTo2dArray(data);
	}
	
	@DataProvider(name="brickAdditionDataNegative")
	public Object[][] getBrickAdditionDataNegative(){
		ArrayList<Object> data = new ArrayList<Object>();
		VolumeMap volumeMap = new VolumeMap();
		
		volumeMap.setPositive(false);// Add bricks already existing on hosts - negative
		volumeMap.setErrorMsg("Error while executing action: Cannot create Gluster Volume. Brick");
		volumeMap.setDeleteDir(false);// Already existing bricks directories should not be deleted in this case
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distribute-with-existing-bricks");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-distribute-1}{server24=bricks-distribute-1}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		data.add(volumeMap);

		return this.convertListTo2dArray(data);
	}
	
	@DataProvider(name="volumeCreationWithAccessProtocols")
	public Object[][] getVolumeCreationDataWithAccessProtocols() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		
		ArrayList<Object> data = new ArrayList<Object>();
		Closure<VolumeMap> basicVolumeFactory= new Closure<VolumeMap>() {
			
			@Override
			public VolumeMap act() {
				VolumeMap volumeMap = new VolumeMap();
				volumeMap.setResourceLocation("System->Volumes");
				volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
				volumeMap.setSpecialCount(0);
				volumeMap.setClusterName("automation_cluster1");
				volumeMap.setVolumeName("automation-volume-distribute-access-protocols");
				volumeMap.setServers("{server23=bricks-distribute-5}{server24=bricks-distribute-5}");
				return volumeMap;
			}
		   
		};
		
		VolumeMap volumeMap = basicVolumeFactory.call();
		volumeMap.setNfsEnabled(true);
		volumeMap.setCifsEnabled(false);
		data.add(volumeMap);
		
		volumeMap = basicVolumeFactory.call();
		volumeMap.setNfsEnabled(false);
		volumeMap.setCifsEnabled(true);
		data.add(volumeMap);

		volumeMap = basicVolumeFactory.call();
		volumeMap.setNfsEnabled(true);
		volumeMap.setCifsEnabled(true);
		data.add(volumeMap);
		
		return this.convertListTo2dArray(data);
	}
	
	
	@DataProvider(name="brickAdditionDataNegativeHostDown")
	public Object[][] getBrickAdditionDataNegativeSetTwo(){
		ArrayList<Object> data = new ArrayList<Object>();
		VolumeMap volumeMap = new VolumeMap();
		
		volumeMap.setDeleteDir(false);// Already existing bricks directories should not be deleted in this case
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster1");
		volumeMap.setVolumeName("automation-volume-distribute-add-brick-host-down");
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-distribute-4}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		data.add(volumeMap);

		return this.convertListTo2dArray(data);
	}

	private VolumeMap getVolumeMapByVolumeName(String volumeName) throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException {
		VolumeMap volumeMap = null;
		Object[][] volumeMapArray = getVolumeCreationData();
		
		for (int x = 0; x < volumeMapArray.length; x++) {
			volumeMap = (VolumeMap) volumeMapArray[x][0];
			if (volumeMap.getVolumeName().equals(volumeName)) {
				return volumeMap;
			}
		}	
		return null;
	}
	
	@DataProvider(name="volumeRejectData")
	public Object[][] getVolumeRejectData(){
		ArrayList<Object> cases = new ArrayList<Object>();
		cases.add("192.168.1.1");
		cases.add("192.168.1.1, 192.168.1.1");
		cases.add("host1");
		cases.add("192.168.1.*");
		cases.add("host1,host2");
		cases.add("*");
		cases.add("255.255.255.255");
		cases.add("0.0.0.0");
		return convertListTo2dArray(cases) ;
	}
	
	@DataProvider(name="volumeRejectNegativeData")
	public Object[][] getVolumeRejectNegativeData(){
		ArrayList<Object> cases = new ArrayList<Object>();
		cases.add("!@#!@#");
		cases.add("adfadsf+adlfkjas");
		cases.add("<script>alert('hi');</script>");
		return convertListTo2dArray(cases) ;
	}
	
	private VolumeMap getVolumeForEmptyClusterData() {
		VolumeMap volumeMap = new VolumeMap();
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName("automation_cluster2"); // assumes automation_cluster2 has no servers.
		volumeMap.setVolumeName("empty_cluster_volume");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		return volumeMap;
	}
	
	@Test
	public void volumeOptionDescriptionTest() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		VolumeMap volume = (VolumeMap)getVolumeCreationData()[0][0];
		Assert.assertTrue(tasks.validateAllVolumeOptionsHaveDescriptions(volume), "not all volume options have descriptions");
	}
	
	@Test
	@Tcms("173321")
	public void brickUpTest() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		VolumeMap volume = (VolumeMap)getVolumeCreationData()[0][0];
		tasks.moveBrickUp(volume);
	}
	
	@Test
	@Tcms("173321")
	public void brickDownTest() throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		VolumeMap volume = (VolumeMap)getVolumeCreationData()[0][0];
		tasks.moveBrickDown(volume);
	}
}
