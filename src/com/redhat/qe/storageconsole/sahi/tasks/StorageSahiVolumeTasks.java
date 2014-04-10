
package com.redhat.qe.storageconsole.sahi.tasks;

import static com.redhat.qe.storageconsole.helpers.AssertUtil.failTest;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import net.sf.sahi.client.ElementStub;
import net.sf.sahi.client.ExecutionException;

import org.testng.Assert;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.redhat.qe.storageconsole.helpers.Closure;
import com.redhat.qe.storageconsole.helpers.Duration;
import com.redhat.qe.storageconsole.helpers.Times;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.jQueryPagePanels;
import com.redhat.qe.storageconsole.helpers.cli.CliVolume;
import com.redhat.qe.storageconsole.helpers.elements.AddBrickDialog;
import com.redhat.qe.storageconsole.helpers.elements.BrickCreationTable;
import com.redhat.qe.storageconsole.helpers.elements.BrickTable;
import com.redhat.qe.storageconsole.helpers.elements.Dialog;
import com.redhat.qe.storageconsole.helpers.elements.ErrorDialog;
import com.redhat.qe.storageconsole.helpers.elements.GwtButton;
import com.redhat.qe.storageconsole.helpers.elements.RemoveBricksDialog;
import com.redhat.qe.storageconsole.helpers.elements.Row;
import com.redhat.qe.storageconsole.helpers.elements.SelectElement;
import com.redhat.qe.storageconsole.helpers.elements.SubTabBrickTable;
import com.redhat.qe.storageconsole.helpers.elements.VolumeTable;
import com.redhat.qe.storageconsole.helpers.elements.tab.VolumeTab;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.helpers.jquery.JsGeneric;
import com.redhat.qe.storageconsole.helpers.ssh.SshResult;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap.EachBrickAction;
import com.redhat.qe.storageconsole.mappper.VolumeMap.EachBrickActionWithResult;
import com.redhat.qe.storageconsole.mappper.VolumeMap.WarningMessage;
import com.redhat.qe.storageconsole.te.Brick;
import com.redhat.qe.storageconsole.te.ClientMachine;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.Sshable;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

import dstywho.timeout.Timeout;

public class StorageSahiVolumeTasks {
	/**
	 * 
	 */
	private static final String LAVENDER = "rgb(195, 208, 224)";
	private static int NUM_ATTEMPTS = 10;
	StorageCLITasks storageCliTasks = new StorageCLITasks();
	StorageBrowser storageSahiTasks = null;
	ElementStub NEAR_REF_VOLUME_OPTIONS_TABLE = null;
	ElementStub NEAR_REF_BRICKS_TABLE = null;
	
	// Create string constants for error messages
	public static final String INVALID_BRICK_DIRECTORY = "Brick Directory should start with '/'";
	public static final String INCORRECT_BRICK_COUNT_STRIPE = "Number of bricks should be equal to Stripe Count";
	public static final String INCORRECT_BRICK_COUNT_DISTRIBUTED_STRIPE = "Number of bricks should be a mutiple of Stripe Count";
	public static final String DUPLICATE_VOLUME_NAME = "already exists.";
	public static final String INCORRECT_BRICK_COUNT_REPLICATE = "Number of bricks should be equal to Replica Count";
	public static final String INCORRECT_BRICK_COUNT_DISTRIBUTED_REPLICATE = "Number of bricks should be a mutiple of Replica Count";
	private static final String BRICK_REMOVAL_CONFIRMATION = "/.*Are you sure you want to remove the following Brick.*/";
    private static final String REPLICATE_CONFIRMATION = "/Multiple bricks of a Replicate volume are present on the same server/";
    private static final String VOLUME_IS_IN_TECHNOLOGY_PREVIEW = "volume is a technology preview feature";
    private static final String VOLUME_IS_UNDER_TECHNOLOGY_PREVIEW = "is under technology preview";
  
	
  private static final String[] ADD_BRICK_MODAL_POSSIBLE_ERROR_MSGS = {
								INVALID_BRICK_DIRECTORY                    
								, INCORRECT_BRICK_COUNT_STRIPE               
								, INCORRECT_BRICK_COUNT_DISTRIBUTED_STRIPE   
								, INCORRECT_BRICK_COUNT_REPLICATE            
								, INCORRECT_BRICK_COUNT_DISTRIBUTED_REPLICATE
							};	



	
	public StorageSahiVolumeTasks(StorageBrowser tasks) {
		storageSahiTasks = tasks;
		NEAR_REF_BRICKS_TABLE = storageSahiTasks.div(6).near(storageSahiTasks.div("Add"));
		NEAR_REF_VOLUME_OPTIONS_TABLE = storageSahiTasks.div("Reset All");
	}
	
	//-------------------------------------------------------------------------------------
	// Create/Remove Start/Stop Volume
	//-------------------------------------------------------------------------------------
	
	/*
	 * Creates a Volume
	 */
	public TaskResult createVolume(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException{
		if(volumeMap.getDeleteDir()){
			deleteDirForBricks(volumeMap);
			createDirForEachBrick(volumeMap);
		}
		
		storageSahiTasks.selectPage(volumeMap.getResourceLocation());		

		//Volume Modal Open
		storageSahiTasks.div("MainTabVolumeView_table_Create_Volume").click();
		storageSahiTasks.select("VolumePopupView_cluster").choose(volumeMap.getClusterName());
		storageSahiTasks.textbox("VolumePopupView_nameEditor").setValue(volumeMap.getVolumeName());
		storageSahiTasks._logger.log(Level.INFO, "Volume Type: "+getVolumeTypeDropDown().getText());
		if(!getVolumeTypeDropDown().getText().equals(volumeMap.getVolumeType())){
			getVolumeTypeDropDown().choose(volumeMap.getVolumeType());
		}
		fillInCreateVolumeModalFields(volumeMap);
		
		
		//Brick Modal Open
		storageSahiTasks.div("VolumePopupView_addBricksButton").click();
		TaskResult addBricksResult = fillInAddBrickModalFields(volumeMap);
		if(addBricksResult.isUnsuccessful()){ return addBricksResult; }
		//Brick Modal Closed

		TaskResult addBrickErrorMessageResult = validateErrorMessagesOnAddBrickModalIfExpecting(volumeMap);
		if(!addBrickErrorMessageResult.isSuccessful()){
			return addBrickErrorMessageResult;
		}
		
		
		Assert.assertEquals(getVolumeTypeDropDown().getValue().toLowerCase(), volumeMap.getVolumeType().toLowerCase(), "volume type selection");
		storageSahiTasks.div("VolumePopupView_onCreateVolume").click();
		//attempt to accept volume changes;attempt to close modal
		
		storageSahiTasks.clickRefresh("Volume");
		if(isWarningMessageIsExpected(volumeMap) && isvalidateWarningMessagePresent(volumeMap)){
			return TaskResult.ERROR_STOP;
		}
		
		if (volumeMap.getVolumeIsTechPreview()) {
			if(!storageSahiTasks.div("/"+VOLUME_IS_IN_TECHNOLOGY_PREVIEW+"/").exists() &&
			   !storageSahiTasks.div("/"+VOLUME_IS_UNDER_TECHNOLOGY_PREVIEW+"/").exists()) {
				failTest("Volume ["+volumeMap.getVolumeName()+"]" + " Tech Preview pop-up did not appear!");
			}
			boolean dialogClosed = false;
			for (int x = 0; x < 3; x++) {
				if (storageSahiTasks.div("Tech Preview").exists()) {
					storageSahiTasks._logger.log(Level.INFO, "Volume ["+volumeMap.getVolumeName()+"] Tech Preview dialog - count ["+(x+1)+"]!");
					storageSahiTasks.div("DefaultConfirmationPopupView_onCreateVolumeInternal").click();
				} else {
					dialogClosed = true;
					break;
				}
			}
	    	Assert.assertTrue(dialogClosed, "Volume ["+volumeMap.getVolumeName()+"] Tech Preview dialog did not close!");
		} else {
			if(storageSahiTasks.div("/"+VOLUME_IS_IN_TECHNOLOGY_PREVIEW+"/").exists() ||
			   storageSahiTasks.div("/"+VOLUME_IS_UNDER_TECHNOLOGY_PREVIEW+"/").exists()) {
			    failTest("Volume ["+volumeMap.getVolumeName()+"]" + " Tech Preview pop-up unexpectedly appeared!");
			}
		}
		
		if(volumeMap.isPositive()) {
			ElementStub volumeNameCell = storageSahiTasks.div(volumeMap.getVolumeName());
			Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(volumeNameCell), 10), "volume cell did not display");
			validateVolumeTableEntry(volumeMap);
		}else{
			validatePresenceOfErrorMessageOnVolumeCreation(volumeMap);
			return TaskResult.EXPECTED_ERROR_RECIEVED;
		}
		return TaskResult.GOOD;
	}

	/**
	 * @param volumeMap
	 */
	private void validatePresenceOfErrorMessageOnVolumeCreation(
			VolumeMap volumeMap) {
		if(!storageSahiTasks.div("/"+volumeMap.getErrorMsg()+"/").exists()){
			failTest("Volume ["+volumeMap.getVolumeName()+"] creation error pop-up did not appear! Error MSG:" + volumeMap.getErrorMsg());
		}else{
			handleExpectedError(volumeMap);
		}
	}

	/**
	 * @param volumeMap
	 */
		// Verify that the cluster displayed for this new volume is correct:
	private void validateVolumeTableEntry(VolumeMap volumeMap) {
		List<HashMap<String, String>> volumesTable = new VolumeTable(storageSahiTasks).getData();

		for (HashMap<String, String> volumeRow : volumesTable) {
			if (volumeMap.getVolumeName().equals(volumeRow.get(GuiTables.NAME))) {
		    	Assert.assertTrue(volumeMap.getClusterName().equals(volumeRow.get(GuiTables.CLUSTER)), "Volume ["+volumeMap.getVolumeName()+"] has invalid Cluster!");
		    	Assert.assertTrue(volumeMap.getVolumeType().equals(volumeRow.get(GuiTables.VOLUME_TYPE)), "Volume Type ["+volumeMap.getVolumeType()+"] is invalid!");
		    	Assert.assertTrue(volumeMap.getBricks().size() == (Integer.parseInt(volumeRow.get(GuiTables.NUMBER_OF_BRICKS))), "Brick Count ["+volumeMap.getBricks().size()+"] is invalid!");
		    	break;
		    }
		}
	}

	/**
	 * @param volumeMap
	 * @return
	 */
	private boolean isVolumePresent(VolumeMap volumeMap) {
		return storageSahiTasks.div(volumeMap.getVolumeName()).exists();
	}

	public boolean moveBrickUp(final VolumeMap volumeMap){
		//get to the brick modal
		
		withAddBrickDialogWithBricksPopulated(volumeMap, new Closure<String>() {
			
			@Override
			public String act() {
				BrickCreationTable brickTable = new BrickCreationTable(storageSahiTasks);
				int indexOfRowToMove = brickTable.getRowCount() - 1;
				ArrayList<String> expectedRow= brickTable.getRow(indexOfRowToMove).getData();
				moveBrickUp(indexOfRowToMove);
				ArrayList<String> movedRow= brickTable.getRow(indexOfRowToMove -1 ).getData();
				Assert.assertEquals(expectedRow, movedRow);
				return null;
			}
		});
		return true;
	}
	public boolean moveBrickDown(final VolumeMap volumeMap){
		//get to the brick modal
		
		withAddBrickDialogWithBricksPopulated(volumeMap, new Closure<String>() {
			
			@Override
			public String act() {
				
				BrickCreationTable brickTable = new BrickCreationTable(storageSahiTasks);
				ArrayList<String> expectedRow= brickTable.getRow(0).getData();
				moveBrickDown(0);
				ArrayList<String> movedRow= brickTable.getRow(1).getData();
				Assert.assertEquals(expectedRow, movedRow);
				return null;
			}
		});
		return true;
	}
	
	public <T> void withAddBrickDialog(VolumeMap volumeMap, Closure<T> actionsToPerformInDialog){
		navigateToAddBricksFromVolumeCreateModal(volumeMap);
		
		actionsToPerformInDialog.act();
		
		cancelAddBrickAndVolumeCreate();
	}
	
	public <T> void withAddBrickDialogWithBricksPopulated(final VolumeMap volumeMap, final Closure<T> actionsToPerformInDialog){
		withAddBrickDialog(volumeMap,new Closure<T>(){

			@Override
			public T act() {
				try{ addBricksWithoutClosingDialog(volumeMap);} catch(Exception e){throw new RuntimeException(e); }
				return actionsToPerformInDialog.call();
			}
		});
	}
	
	public void moveBrickUp(int index){
		new BrickCreationTable(storageSahiTasks).checkRow(index);
		storageSahiTasks.div("AddBrickPopupView_moveBricksUpButton").click();
	}

	public void moveBrickDown(int index){
		new BrickCreationTable(storageSahiTasks).checkRow(index);
		storageSahiTasks.div("AddBrickPopupView_moveBricksDownButton").click();
	}
	
	/**
	 * @param volumeMap
	 */
	private void navigateToAddBricksFromVolumeCreateModal(VolumeMap volumeMap) {
		storageSahiTasks.selectPage(volumeMap.getResourceLocation());
		storageSahiTasks.div("MainTabVolumeView_table_Create_Volume").click();
		fillInCreateVolumeModalFields(volumeMap);
		storageSahiTasks.div("VolumePopupView_addBricksButton").click();
	}


	/**
	 * @param volumeMap
	 */
	private void handleExpectedError(VolumeMap volumeMap) {
		storageSahiTasks._logger.log(Level.INFO, "Volume ["+volumeMap.getVolumeName()+"] creation error pop-up appeared!");
		storageSahiTasks._logger.log(Level.INFO, "Volume ["+volumeMap.getVolumeName()+"] creation failed as expected!");
		storageSahiTasks.closePopup("Close");
		storageSahiTasks.div("VolumePopupView_Cancel").click();
	}

	/**
	 * @return
	 */
	private ElementStub getVolumeTypeDropDown() {
		return storageSahiTasks.select("VolumePopupView_typeListEditor");
	}

	/**
	 * @param volumeMap
	 */
	private void createDirForEachBrick(VolumeMap volumeMap) {
		volumeMap.forEachBrick(new EachBrickAction() {
			
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				try {
					SshResult result = storageCliTasks.createDir(server, brick.getLocation());
					if(!result.isSuccessful()){
						throw new RuntimeException("unable to create brick dir;" + result.getStderr());
					}
				} catch (IOException e) {
					throw new RuntimeException("unable to create brick dir");
				}
			}
		});
		
	}

	private void fillInCreateVolumeModalFields(VolumeMap volumeMap) {
		storageSahiTasks.select("VolumePopupView_cluster").choose(volumeMap.getClusterName());
		storageSahiTasks.textbox("VolumePopupView_nameEditor").setValue(volumeMap.getVolumeName());
		storageSahiTasks._logger.log(Level.INFO, "Volume Type: "+storageSahiTasks.select("VolumePopupView_typeListEditor").getText());
		if(!storageSahiTasks.select("VolumePopupView_typeListEditor").getText().equals(volumeMap.getVolumeType())){
			storageSahiTasks.select("VolumePopupView_typeListEditor").choose(volumeMap.getVolumeType());
		}
		checkOrUncheckNfs(volumeMap);
		checkorUncheckCifsCheckbox(volumeMap);
		setAllowAccess(volumeMap);
	}

	/**
	 * @param volumeMap
	 * @return 
	 * @throws TestEnvironmentConfigException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JAXBException
	 */
	private TaskResult fillInAddBrickModalFields(VolumeMap volumeMap)
			throws TestEnvironmentConfigException, FileNotFoundException,
			IOException, JAXBException {
		setReplicaOrStripeCount(volumeMap);
		return addBricks(volumeMap);
	}

	/**
	 * @param volumeMap
	 */
	private void checkOrUncheckNfs(VolumeMap volumeMap) {
		if(volumeMap.isNfsEnabled()){
			storageSahiTasks.checkbox("VolumePopupView_nfs_accecssProtocolEditor").check();
		}else{
			storageSahiTasks.checkbox("VolumePopupView_nfs_accecssProtocolEditor").uncheck();
		}
	}

	/**
	 * @param volumeMap
	 */
	private void setAllowAccess(VolumeMap volumeMap) {
		if((volumeMap.getClientMachines() != null) && volumeMap.getClientMachines().size()>0){//Adding the list of allowed hostnames to the auth.allow list
			List<ClientMachine> clientMachines = volumeMap.getClientMachines();
			String authAllowList = null;
			for (ClientMachine clientMachine : clientMachines) {
				if(clientMachine.equals(clientMachines.get(0))){
					authAllowList = clientMachine.getHostname();
				} else {
					authAllowList = authAllowList+","+clientMachine.getHostname();
				}
			}
			storageSahiTasks.textbox("VolumePopupView_allowAccessEditor").setValue(authAllowList);
		}
	}
	
	/**
	 * @param volumeMap
	 */
	private void setReplicaOrStripeCount(VolumeMap volumeMap) {
		if(volumeMap.getVolumeType().equals(VolumeMap.VolumeType.DISTRIBUTE.toString())){
			//Nothing to do
		}else if(volumeMap.getVolumeType().equals(VolumeMap.VolumeType.REPLICATE.toString())){
			storageSahiTasks.textbox("AddBrickPopupView_replicaCountEditor").setValue(String.valueOf(volumeMap.getSpecialCount()));
		}else if(volumeMap.getVolumeType().equals(VolumeMap.VolumeType.DISTRIBUTED_REPLICATE.toString())){
			storageSahiTasks.textbox("AddBrickPopupView_replicaCountEditor").setValue(String.valueOf(volumeMap.getSpecialCount()));
		}else if(volumeMap.getVolumeType().equals(VolumeMap.VolumeType.STRIPE.toString())){
			storageSahiTasks.textbox("AddBrickPopupView_stripeCountEditor").setValue(String.valueOf(volumeMap.getSpecialCount()));
		}else if(volumeMap.getVolumeType().equals(VolumeMap.VolumeType.DISTRIBUTED_STRIPE.toString())){
			storageSahiTasks.textbox("AddBrickPopupView_stripeCountEditor").setValue(String.valueOf(volumeMap.getSpecialCount()));
		}
	}
	
	/**
	 * @param volumeMap
	 * @return
	 */
	private boolean isvalidateWarningMessagePresent(VolumeMap volumeMap) {
		if(isWarningMessageIsExpected(volumeMap)){
			if(	isWarningPresent(volumeMap.getWarningMsg())){
				storageSahiTasks.closePopup("Close");
				return true;
			}else{
				storageSahiTasks._logger.log(Level.WARNING, "Expected Warning Message was not displayed");
				return false;
			}
		}else{
			return true;
		}
	}

	/**
	 * @param volumeMap
	 * @return
	 */
	private boolean isWarningMessageIsExpected(VolumeMap volumeMap) {
		return volumeMap.getWarningMsg() != null;
	}
	
	private void checkorUncheckCifsCheckbox(VolumeMap volumeMap) {
		if(volumeMap.isCifsEnabled()){
			storageSahiTasks.checkbox("VolumePopupView_cifs_accecssProtocolEditor").check();
		}else{
			storageSahiTasks.checkbox("VolumePopupView_cifs_accecssProtocolEditor").uncheck();
		}
	}
	
	/*
	 * Removes a Volume
	 */
	public boolean removeVolume(VolumeMap volumeMap){
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		
		storageSahiTasks.clickRefresh("Volume");
		
		//Stop the Volume if it's in running state
		stopVolume(volumeMap);
		
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.div("MainTabVolumeView_table_Remove").click();
		storageSahiTasks.div("RemoveConfirmationPopupView_OnRemove").click();
		storageSahiTasks.clickRefresh("Volume");
		if(isVolumePresent(volumeMap)){
			storageSahiTasks._logger.log(Level.WARNING, "Volume["+volumeMap.getVolumeName()+" is available on the list!");
			return false;
		}
		return true;
	}
	
	/*
	 * Start a Volume
	 */
	public boolean startVolume(final VolumeMap volumeMap){
		storageSahiTasks.selectPage(volumeMap.getResourceLocation());		
		storageSahiTasks.clickRefresh("Volume");
		
		VolumeTab volumeTab = new VolumeTab(storageSahiTasks);
		volumeTab.waitUntilArrived();
		
		ElementStub volumeNameCell = storageSahiTasks.div(volumeMap.getVolumeName());
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(volumeNameCell), 20), "volume cell did not display");

		VolumeTable table = new VolumeTable(storageSahiTasks);
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(table.getElementStub()), 20), "volume table is visible");
		Assert.assertFalse(table.isStatusUp(table.getFirstRowIndexThatContainsText(volumeMap.getVolumeName())), "Volume ["+volumeMap.getVolumeName()+"] is in running state, Nothing to do!");
		
		volumeNameCell.click();
		waitUntilTableItemIsSelected(volumeNameCell);
		
		ElementStub startButton = storageSahiTasks.div("MainTabVolumeView_table_Start");
		Assert.assertTrue(new GwtButton(JQuery.toJQuery(startButton), storageSahiTasks).waitUntilButtonEnabled(), "start button did not become enabled");
		
		startVolumeMultipleTimesIfFail(volumeMap, startButton);
		return true;
	}

	/**
	 * @param volumeMap
	 * @param startButton
	 */
	private void startVolumeMultipleTimesIfFail(final VolumeMap volumeMap,
			ElementStub startButton) {
		for(int attempt : new Times(3)){
			if(clickStartVolumeButtonIsSuccessful(volumeMap, startButton))
				return;
		}
		Assert.fail("Volume ["+volumeMap.getVolumeName()+"] is did not start!");
	}

	/**
	 * @param volumeMap
	 * @param startButton
	 */
	private boolean clickStartVolumeButtonIsSuccessful(final VolumeMap volumeMap,
			ElementStub startButton) {
		startButton.click();
		return WaitUtil.waitUntil(new Predicate<Integer>() {
			
			@Override
			public boolean apply(Integer arg0) {
				VolumeTable volumeTable = new VolumeTable(storageSahiTasks);
				return volumeTable.isStatusUp(volumeTable.getFirstRowIndexThatContainsText(volumeMap.getVolumeName()));
			}
		}, NUM_ATTEMPTS);
	}
	
	/*
	 * Stop a Volume
	 */
	public boolean stopVolume(VolumeMap volumeMap){
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		
		VolumeTab volumeTab = new VolumeTab(storageSahiTasks);
		volumeTab.waitUntilArrived();
		
		ElementStub volumeNameCell = storageSahiTasks.div(volumeMap.getVolumeName());
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(volumeNameCell), 10), "volume cell did not display");
		
		VolumeTable table = new VolumeTable(storageSahiTasks);
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(table.getElementStub()), 10), "volume table is visible");
		
		if (table.isStatusDown(table.getFirstRowIndexThatContainsText(volumeMap.getVolumeName()))) {
			storageSahiTasks._logger.log(Level.INFO, "Volume ["+volumeMap.getVolumeName()+"] is not in running state, Nothing to do!");
			return true;
		}
		
		volumeNameCell.click();
		waitUntilTableItemIsSelected(volumeNameCell);
		
		ElementStub stopButton = storageSahiTasks.div("MainTabVolumeView_table_Stop");
		clickStopVolumeButtonIsSuccessful(volumeMap, stopButton);
		
		return true;
	}
	

	private boolean clickStopVolumeButtonIsSuccessful(final VolumeMap volumeMap, ElementStub stopButton) {
		stopButton.click();
		storageSahiTasks.closePopup("OK");
		return waitForVolumeToBeDown(volumeMap);
	}

	/**
	 * @param volumeMap
	 * @return
	 */
	public boolean waitForVolumeToBeDown(final VolumeMap volumeMap) {
		return WaitUtil.waitUntil(new Predicate<Integer>() {
			
			@Override
			public boolean apply(Integer arg0) {
				VolumeTable volumeTable = new VolumeTable(storageSahiTasks);
				return volumeTable.isStatusDown(volumeTable.getFirstRowIndexThatContainsText(volumeMap.getVolumeName()));
			}
		}, NUM_ATTEMPTS);
	}
	

	public static class TaskResult {
		private state_value value;
		/**
		 * @param expectedErrorRecieved
		 */
		public TaskResult(state_value value) {
			this.value = value;
		}
		public static enum state_value { EXPECTED_ERROR_RECIEVED, GOOD, ERROR_STOP }
		public static final TaskResult EXPECTED_ERROR_RECIEVED = new TaskResult(state_value.EXPECTED_ERROR_RECIEVED);
		public static final TaskResult GOOD = new TaskResult(state_value.GOOD);
		public static final TaskResult ERROR_STOP = new TaskResult(state_value.ERROR_STOP);
		
		public boolean isSuccessful(){
			return isContinueExecuting();
		}
		public boolean isUnsuccessful(){
			return !isContinueExecuting();
		}
		
		public boolean isStopExecuting(){
			return !isContinueExecuting();
		}
		
		public boolean isContinueExecuting(){
			if(value.equals(state_value.GOOD)){
				return true;
			}else{
				return false;
			}
		}
	};

	//-------------------------------------------------------------------------------------
	// Add bricks
	//-------------------------------------------------------------------------------------
	/**
	 * @param server
	 * @return 
	 */
	private void validatePresenceOfBrickInBrickCreationTable(Server server, Brick brick) {
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(new BrickCreationTable(storageSahiTasks).getElementStub()),NUM_ATTEMPTS ), "brick creation table is visble");
		ArrayList<Row> rowsForServer = new BrickCreationTable(storageSahiTasks).getRowsThatContainsText(server.getHostname());
		Assert.assertTrue(rowsForServer.size() > 0, "no brick entry created in brick creation table.");
		for(Row row: rowsForServer){
			if(row.getText().contains(brick.getLocation()))
				return ;
		}
		Assert.fail("no brick entry created in brick creation table");
	}

	public TaskResult addBricksWithoutClosingDialog(final VolumeMap volumeMap) throws ExecutionException, TestEnvironmentConfigException, FileNotFoundException, IOException, JAXBException{
		
		return volumeMap.forEachBrickWithResult(new EachBrickActionWithResult() {
			
			@Override
			public TaskResult perform(Server server, String brickSetName, Brick brick) {
				storageSahiTasks.select("AddBrickPopupView_serverEditor").choose(server.getHostname());
				storageSahiTasks.textbox("AddBrickPopupView_exportDirEditor").setValue(brick.getLocation());
				storageSahiTasks.div("AddBrickPopupView_addBrickButton").click();
				if(isValidationInvalidBrickDirExpected(volumeMap)){
					validatePresenceOfInvalidBrickDirErrorMessage(volumeMap);
					return TaskResult.EXPECTED_ERROR_RECIEVED;
				}else{
					// validatePresenceOfBrickInBrickCreationTable(server,brick);
					return TaskResult.GOOD;
				}
			}
			
			
		});
	}
	public TaskResult addBricks(final VolumeMap volumeMap) throws ExecutionException, TestEnvironmentConfigException, FileNotFoundException, IOException, JAXBException{
		
		TaskResult result = addBricksWithoutClosingDialog(volumeMap);
		if(result.isStopExecuting()){
			return result;
		}else{
			if(storageSahiTasks.checkbox("AddBrickPopupView_forceEditor").isVisible())
				storageSahiTasks.checkbox("AddBrickPopupView_forceEditor").check();
	        storageSahiTasks.div("AddBrickPopupView_OnAddBricks").click(); 
	        acceptConfirmationIfExists(volumeMap);
			return TaskResult.GOOD;
		}
	}

	/**
	 * @param volumeMap
	 */
	private void acceptConfirmationIfExists(final VolumeMap volumeMap) {
		if (storageSahiTasks.div(REPLICATE_CONFIRMATION).exists()) {		
                storageSahiTasks._logger.log(Level.INFO, "Volume ["+volumeMap.getVolumeName()+"] Replicate Confirmation!");
                storageSahiTasks.closePopup("Yes");
        }
	}

	
	private boolean isValidationInvalidBrickDirExpected(VolumeMap volumeMap){
		return volumeMap.getErrorMsg()!=null && volumeMap.getErrorMsg().equals(INVALID_BRICK_DIRECTORY);	
	}
	/**
	 * @param volumeMap
	 */
	private TaskResult validatePresenceOfInvalidBrickDirErrorMessage(VolumeMap volumeMap) {
		if(storageSahiTasks.div(volumeMap.getErrorMsg()).exists()){
			cancelAddBrickAndVolumeCreate();
			return TaskResult.EXPECTED_ERROR_RECIEVED;
		}else{
			failTest("Error message \"Invalid Brick Directory\" did not appear!");
		}
		return TaskResult.ERROR_STOP;
	}

	/**
	 * 
	 */
	private void cancelAddBrickAndVolumeCreate() {
		storageSahiTasks.div("AddBrickPopupView_Cancel").click();
		storageSahiTasks.div("VolumePopupView_Cancel").click();
	}
	
	private TaskResult validateErrorMessagesOnAddBrickModalIfExpecting(VolumeMap volumeMap){
		/*If the test is to check if an error message appears in case of number of bricks not being equal to stripe count for a volume of type stripe or distributed stripe or
		  in case of the number of bricks not being equal to replica count for a volume of type replicate or distributed-replicate*/
		if(volumeMap.getErrorMsg()!=null){
			if(Arrays.asList(ADD_BRICK_MODAL_POSSIBLE_ERROR_MSGS).contains(volumeMap.getErrorMsg())){
				return validateErrorMessageOnAddBrickModal(volumeMap);
			}
		}else{
			return TaskResult.GOOD;
		}
		return TaskResult.GOOD;
	}

	/**
	 * @param volumeMap
	 * @return
	 */
	private TaskResult validateErrorMessageOnAddBrickModal(VolumeMap volumeMap) {
		if(storageSahiTasks.div(volumeMap.getErrorMsg()).exists()){
			storageSahiTasks._logger.log(Level.INFO, "Volume ["+volumeMap.getVolumeName()+"] creation error message appeared!");
			storageSahiTasks._logger.log(Level.INFO, "Volume ["+volumeMap.getVolumeName()+"] creation failed as expected!");
			cancelAddBrickAndVolumeCreate();
			return TaskResult.EXPECTED_ERROR_RECIEVED;
		}else{
			failTest( "Volume ["+volumeMap.getVolumeName()+"] brick addition error message did not appear!");
			return TaskResult.ERROR_STOP;
		}
	}
	
	//-------------------------------------------------------------------------------------
	// Add bricks to existing volume
	//-------------------------------------------------------------------------------------
	public boolean addBricksExistingVolume(final VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException {
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		deleteDirForBricks(volumeMap);
		
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.link("Bricks").click();
		storageSahiTasks.div("Add").click();

		if(! addBricksWithoutClosingDialog(volumeMap).isSuccessful())  return false;
		if(storageSahiTasks.checkbox("AddBrickPopupView_forceEditor").isVisible())
			storageSahiTasks.checkbox("AddBrickPopupView_forceEditor").check();
		new Dialog("Add Bricks", storageSahiTasks).getOkButton().getElementStub().click();
		Assert.assertFalse(new AddBrickDialog(storageSahiTasks).hasValidationMessages(),"brick dialog has validation messages");
		verifyErrorDialogIsNotVisible();
		validateErrorMessagesOnAddBrickModalIfExpecting(volumeMap);
		return true;
	}

	/**
	 * 
	 */
	private void verifyErrorDialogIsNotVisible() {
		ErrorDialog errorDialog = new ErrorDialog(storageSahiTasks);
		Assert.assertFalse(errorDialog.waitUntilVisible(Timeout.TIMEOUT_FIVE_SECONDS), "error dialog displayed: " + errorDialog.getText());
	}

	/**
	 * @param volumeMap
	 * @param currentBrickRowCount
	 */
	private boolean waitForVolumeBrickCount(final VolumeMap volumeMap, final int expectedRowCount) {
		return WaitUtil.waitUntil(new Predicate<Integer>() {
			public boolean apply(Integer attempt){
				Duration.ONE_SECOND.sleep();
				storageSahiTasks.clickRefresh("Volume");
				int actualRowCount = GuiTables.getBricksTable(storageSahiTasks, NEAR_REF_BRICKS_TABLE).size();
				String str = String.format("Volume [%s]: expected number bricks [%s], actual number of bricks [%s]"
						, volumeMap.getVolumeName(), expectedRowCount, actualRowCount);
				return actualRowCount ==  expectedRowCount;
			}
		}, 10 );
	}
	
	//-------------------------------------------------------------------------------------
	// Add Volume Option
	//-------------------------------------------------------------------------------------
	public boolean addVolumeOption(VolumeMap volumeMap)  {
		String optionName = volumeMap.getVolumeOptionKey();
		String optionValue = volumeMap.getVolumeOptionValue();
		int wait = 1000;
		int retryCount = 35;
		
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())) {
			return false;
		}
		
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(storageSahiTasks.div("Volume Options")), 5);		
		storageSahiTasks.link("Volume Options").click();
		WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(storageSahiTasks.div("Add")), 5);
		storageSahiTasks.div("Add").click();
		storageSahiTasks.select("VolumeParameterPopupView_keyListBox").choose(optionName);
		storageSahiTasks.textbox(0).near(storageSahiTasks.label("Option Value")).setValue(optionValue);
		storageSahiTasks.div("VolumeParameterPopupView_OnSetParameter").click();
		storageSahiTasks.clickRefresh("Volume");
		
		if (!waitForVolumeOptionToDisplay(optionName, wait, retryCount)){ return false;	}
		
		if(!storageSahiTasks.div(optionName).exists()) {
			storageSahiTasks._logger.log(Level.WARNING, "Volume ["+volumeMap.getVolumeName()+"] Option [" + optionName + "] failed to add.");
			return false;
		}
		
		storageSahiTasks._logger.log(Level.INFO, "Volume ["+volumeMap.getVolumeName()+"] Option [" + optionName + "] added.");
		
		return true;
	}
	
	private boolean waitForVolumeOptionToDisplay(String optionName, int wait,
			int retryCount) {
		for (int x = 0; x < retryCount; x++) {
			if(storageSahiTasks.div("/Error.*/").exists()){
				storageSahiTasks._logger.log(Level.WARNING, "Volume Option [" + optionName + "] failed to add.");
				storageSahiTasks.closePopup("Close");
				storageSahiTasks.closePopup("Cancel");
				return false;
			}else{
				if(storageSahiTasks.div(optionName).exists()) {
					storageSahiTasks._logger.log(Level.FINE, "Volume Option [" + optionName + "] found.");
					return true;
				} else {
					storageSahiTasks.wait(wait, retryCount, x);
				}
			}
		}
		return true;
	}
	
	
	
	
	
	//-------------------------------------------------------------------------------------
	// Reset Volume Option
	//-------------------------------------------------------------------------------------
	public boolean resetVolumeOption(VolumeMap volumeMap) {
		String optionName = volumeMap.getVolumeOptionKey();
		int wait = 1000;
		int retryCount = 60;
		
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())) {
			return false;
		}
		
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.link("Volume Options").click();
		
		if (!storageSahiTasks.div(optionName).exists()) {
			storageSahiTasks._logger.log(Level.INFO, "Volume ["+volumeMap.getVolumeName()+"] Option [" + optionName + "] is not present, Nothing to do!");
			return true;
		}
		
		storageSahiTasks._logger.log(Level.FINE, "Option Name: "+optionName);
		
		selectVolumeAndClickResetTwice(optionName);
		waitForResetOptionConfirmationToDisplay();
		
		storageSahiTasks.div("DefaultConfirmationPopupView_OnResetParameter").click();
		storageSahiTasks.clickRefresh("Volume");
		
		for (int x = 0; x < retryCount; x++) {
			if(!storageSahiTasks.div(optionName).exists()) {
				break;
			} else {
				storageSahiTasks.wait(wait, retryCount, x);
			}
		}
		
		if (storageSahiTasks.div(optionName).exists()) {
			storageSahiTasks._logger.log(Level.WARNING, "Volume [" + volumeMap.getVolumeName() + "] Option [" + optionName + "] failed to remove.");
			return false;
		}
		
		storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] Option [" + optionName + "] removed.");
		return true;
	}
	
	/**
	 * 
	 */
	private void waitForResetOptionConfirmationToDisplay() {
		for(int numTries =0; numTries<5; numTries++){
			if(storageSahiTasks.div("DefaultConfirmationPopupView_OnResetParameter").isVisible()) return;
		}
		Assert.assertFalse(true, "reset option confirmation never displayed");
	}
	
	/**
	 * @param optionName
	 */
	private void selectVolumeAndClickResetTwice(String optionName) {
		for(int i=0; i < 2 ;i ++){
			storageSahiTasks.div(optionName).click();
			storageSahiTasks.div("Reset").click();
		}
	}
	
	//-------------------------------------------------------------------------------------
	// Edit Volume Option
	//-------------------------------------------------------------------------------------
	public boolean editVolumeOption(VolumeMap volumeMap)  {
		String optionName = volumeMap.getVolumeOptionKey();
		String editOptionValue = volumeMap.getVolumeEditOptionValue();
		//String optionValue = null;
		//int rowNumber = -1;
		int wait = 1500;
		int retryCount = 60;
		
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())) {
			return false;
		}
		
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.link("Volume Options").click();
		
		if (!storageSahiTasks.div(optionName).exists()) {
			storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] Option [" + optionName + "] is not present, Nothing to do!");
			return true;
		}
		
		/*rowNumber = GuiTables.getRowNumber(storageSahiTasks, GuiTables.VOLUME_OPTION_TABLE_REFERENCE, optionName);
		if (rowNumber < 0) {
			storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] Option [" + optionName + "] + Option Value [" + editOptionValue + "] failed to find Option row.");
			return false;
		}*/
		
		if (GuiTables.getVolumeOption(storageSahiTasks, optionName, NEAR_REF_VOLUME_OPTIONS_TABLE) == null) {
			storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] Option [" + optionName + "] + Option Value [" + editOptionValue + "] failed to find Option row.");
			return false;
		}
		
		storageSahiTasks.div(optionName).click();
		storageSahiTasks.div(2).near(storageSahiTasks.div("Add")).click();
		storageSahiTasks.textbox(0).near(storageSahiTasks.label("Option Value")).setValue(editOptionValue);
		storageSahiTasks.div("VolumeParameterPopupView_OnSetParameter").click();  // Click Edit Option OK button
		storageSahiTasks.clickRefresh("Volume");
		
		HashMap<String, String> optionKeyValueMap = null;
		for (int x = 0; x < retryCount; x++) {
			/*rowNumber = GuiTables.getRowNumber(storageSahiTasks, GuiTables.VOLUME_OPTION_TABLE_REFERENCE, optionName);
			optionValue = storageSahiTasks.div("/" + GuiTables.GENERAL_TABLE_REFERENCE+"1_row"+ rowNumber + "/").getText().trim();
			if (optionValue.equals(editOptionValue)) {
				break;
			}*/
			optionKeyValueMap = GuiTables.getVolumeOption(storageSahiTasks, optionName, NEAR_REF_VOLUME_OPTIONS_TABLE);
			if (optionKeyValueMap != null) {
				if(optionKeyValueMap.get(GuiTables.VOLUME_OPTION_VALUE).equals(editOptionValue)){
					break;
				}
			}
			storageSahiTasks._logger.log(Level.FINE, "Attempt: [" + (x+1) + " of " + retryCount + "]");
			storageSahiTasks.waitFor(wait);
		}
		
		//if (!optionValue.equals(editOptionValue)) {
		if(!optionKeyValueMap.get(GuiTables.VOLUME_OPTION_VALUE).equals(editOptionValue)){
			storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] Option [" + optionName + "] + Option Value [" + editOptionValue + "] failed to edited.");
			return false;
		}
		
		storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] Option [" + optionName + "] + Option Value [" + editOptionValue + "] edited.");
		return true;
	}
	
	//-------------------------------------------------------------------------------------
	// Reset All Volume Options
	//-------------------------------------------------------------------------------------
	public boolean resetAllVolumeOptions(VolumeMap volumeMap) {
		int wait = 1500;
		int retryCount = 35;
		//String cellReference = "/" + GuiTables.VOLUME_OPTION_TABLE_REFERENCE + "/";
		
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())) {
			return false;
		}
		
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.link("Volume Options").click();
		
		//if (storageSahiTasks.div(cellReference).countSimilar() == 0) {
		if (GuiTables.getVolumesOptionsTable(storageSahiTasks, NEAR_REF_VOLUME_OPTIONS_TABLE).size() == 0) {
			storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] no Volume Options present, Nothing to do!");
			return true;
		}
		
		storageSahiTasks.div("Reset All").click();
		storageSahiTasks.div("DefaultConfirmationPopupView_OnResetAllParameters").click();
		storageSahiTasks.clickRefresh("Volume");
		
		for (int x = 0; x < retryCount; x++) {
			//if(storageSahiTasks.div(cellReference).countSimilar() == 0) {
			if (GuiTables.getVolumesOptionsTable(storageSahiTasks, NEAR_REF_VOLUME_OPTIONS_TABLE).size() == 0) {
				break;
			} else {
				storageSahiTasks.wait(wait, retryCount, x);
			}
		}
		
		//if (storageSahiTasks.div(cellReference).countSimilar() != 0) {
		if (GuiTables.getVolumesOptionsTable(storageSahiTasks, NEAR_REF_VOLUME_OPTIONS_TABLE).size() != 0) {
			storageSahiTasks._logger.log(Level.WARNING, "Volume [" + volumeMap.getVolumeName() + "] Reset All Options failed to remove.");
			return false;
		}
		
		storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] all Volume Options removed.");
		return true;
	}
	
	//-------------------------------------------------------------------------------------
	// Create directory for bricks
	//-------------------------------------------------------------------------------------
	
	public boolean deleteDirForBricks(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		
		HashMap<String, String> serversBricks = storageSahiTasks.getListMapFromString(volumeMap.getServers());
		List<Server> servers = storageSahiTasks.getServers(serversBricks.keySet().toString().replaceAll("[\\[\\]]", ""));
		for(Server server : servers){
			if(!storageCliTasks.deleteBricksDirs(server, serversBricks.get(server.getName()).trim())){
				failTest("Server ["+server.getHostname()+"] failed to delete brick directories!");
			}
		}
		return true;
	}
	
	/*
	 * Testing volume option auth.allow
	 * 
	 * Precondition: this test assumes that authAllow value for the Volume contains at least 2 values. 1 of the values must be the clientMachine.
	 *
	 * Steps being followed -
	 *  - Set the value of "Allow Access From" textbox in the volume creation pop-up to the list of client machine hostnames.
	 *  - Try to mount the volume from one of the machines listed in the "Allow Access From" list, mount should succeed.
	 *  - Now, remove the hostname of the client where volume is currently mounted from the list of allowed hostnames.
	 *  - Unmount the volume from that client.
	 *  - Try to mount the volume at the same client again, mount should fail.
	 */
	public boolean testAuthAllow(VolumeMap volumeMap) throws IOException, TestEnvironmentConfigException, JAXBException {
		ClientMachine clientMachine = volumeMap.getClientMachines().get(0);
		String mountPoint = clientMachine.getMountPoints().get(0)+volumeMap.getVolumeName();// Path for the mount point
		
		Assert.assertTrue(storageCliTasks.mountVolume(volumeMap, mountPoint, true), 
				"Unable to mount the volume ["+ volumeMap.getVolumeName() + "] at the mount point ["+ mountPoint +"] on the machine ["+volumeMap.getClientMachines().get(0).getHostname()+"]");
		
		removeFromAuthAllowList(volumeMap, clientMachine.getHostname());// Clear the client hostname from list
		
		Assert.assertFalse(storageCliTasks.mountVolume(volumeMap, mountPoint, false), 
				"Able to mount the volume ["+ volumeMap.getVolumeName() + "] at the mount point ["+ mountPoint +"]on machine ["+volumeMap.getClientMachines().get(0).getHostname()+"]. Should not be able to mount volume!");
		
		return true;
	}

	/**
	 * @param volumeMap
	 */
	private void removeFromAuthAllowList(VolumeMap volumeMap, String hostname) {
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.link("Volume Options").click();
		WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(storageSahiTasks.div("auth.allow")), 10);
		storageSahiTasks.div("auth.allow").click();
		storageSahiTasks.div(2).near(storageSahiTasks.div("Add")).click();// Clicking on Edit button
		String authAllowListValue = storageSahiTasks.textbox(0).near(storageSahiTasks.label("Option Value")).getValue();
		ArrayList<String> authAllowValues = new ArrayList<String>(Arrays.asList(authAllowListValue.split("\\s*,\\s*")));
		authAllowValues.remove(hostname);
		storageSahiTasks.textbox(0).near(storageSahiTasks.label("Option Value")).setValue(Joiner.on(",").join(authAllowValues));
		storageSahiTasks.closePopup("OK");
		WaitUtil.waitUntil(new Predicate<Integer>() {
			public boolean apply(Integer attempt){
				ElementStub editVolumeOptionTitle = storageSahiTasks.label("Edit Option");
				return !(editVolumeOptionTitle.isVisible());
			}
		}, 20);
	}
	
	/*
	 * Testing addition of brick on a host that is down - negative
	 *
	 * Steps being followed -
	 *  - Bring a host down by stopping vdsmd on that host
	 *  - Try to add brick on that host - ideally the host will not appear in the drop down menu for selection of servers to add bricks.
	 */
	public boolean addBrickHostDown(VolumeMap volumeMap) throws Exception{
		HashMap<String, String> serversBricks = storageSahiTasks.getListMapFromString(volumeMap.getServers());
		List<Server> servers = storageSahiTasks.getServers(serversBricks.keySet().toString().replaceAll("[\\[\\]]", ""));
		
		if(!storageCliTasks.bringHostDown(servers.get(0))){//Stop vdsmd
			storageSahiTasks._logger.log(Level.SEVERE, "Unable to stop vdsmd on host["+servers.get(0).getHostname()+"]!");
			return false;
		}
		if(!storageCliTasks.stopGlusterd(servers.get(0))){//Stop glusterd
			storageSahiTasks._logger.log(Level.SEVERE, "Unable to stop glusterd on host["+servers.get(0).getHostname()+"]!");
			return false;
		}
		try {
			if (createVolume(volumeMap).isSuccessful()) {
				return false;
			} else {
				return true;
			}
		} catch(Exception ex){
			if(!storageCliTasks.startGlusterd(servers.get(0))){//Start glusterd
				storageSahiTasks._logger.log(Level.SEVERE, "Unable to start glusterd on host["+servers.get(0).getHostname()+"]!");
				return false;
			}
			//storageCliTasks.bringHostUp(servers.get(0));
			if(!storageCliTasks.bringHostUp(servers.get(0))){//Start vdsmd
				storageSahiTasks._logger.log(Level.SEVERE, "Unable to start vdsmd on host["+servers.get(0).getHostname()+"]!");
				return false;
			}
			if(!storageSahiTasks.getString(ex).contains("Option not found: "+servers.get(0).getHostname())){
				storageSahiTasks._logger.log(Level.SEVERE,"Exception stack trace not containing expected message!");
				throw(ex);
			}
			storageSahiTasks._logger.log(Level.INFO,"Exception stack trace contains expected message!");
			cancelAddBrickAndVolumeCreate();
			return true;
		}
	}
	
	/*
	 * Will remove 1 brick, and then 2 bricks.
	 */
	public boolean removeBricks(VolumeMap volumeMap) {
		String volumeName =  volumeMap.getVolumeName();
		
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())) {
			return false;
		}
		
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.link("Bricks").click();
		
		int currentBrickRowCount = GuiTables.getBricksTable(storageSahiTasks, NEAR_REF_BRICKS_TABLE).size();
		storageSahiTasks._logger.log(Level.FINE, "Number of Bricks: "+currentBrickRowCount);
		Assert.assertTrue(currentBrickRowCount > 0, "Volume [" + volumeName + "] no bricks present!");
		
		// Remove single brick
		storageSahiTasks.clickRefresh("Volume"); // Be sure page is loaded
		final ElementStub firstRow = getVolumeBrickRow(0);
		clickVolumeBrickRow(firstRow);
		validateBrickRowIsSelected(firstRow);
		boolean result = removeBricksAndWaitForCompletion(volumeName, (currentBrickRowCount - 1));
		if (result) {
			storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] one brick removed.");
		} else {
			storageSahiTasks._logger.log(Level.WARNING, "Volume [" + volumeMap.getVolumeName() + "] failed to remove one brick!");
			return false;
		}
		
		// Remove two bricks
		
		currentBrickRowCount = GuiTables.getBricksTable(storageSahiTasks, NEAR_REF_BRICKS_TABLE).size();
		storageSahiTasks._logger.log(Level.FINE, "Number of Brick(s): "+currentBrickRowCount);
		if (currentBrickRowCount < 2) {
			storageSahiTasks._logger.log(Level.WARNING, "Volume [" + volumeName + "] need two bricks to proceed!");
			return false;
		}
		
		storageSahiTasks.clickRefresh("Volume");
		clickVolumeBrickRow(firstRow);
		validateBrickRowIsSelected(firstRow);
		ElementStub fourthRow = getVolumeBrickRow(3);
		storageSahiTasks.execute(String.format("_sahi._click(%s, 'CTRL');", fourthRow.toString()));   // Select 2nd row
		validateBrickRowIsSelected(fourthRow);
		
		
		result = removeBricksAndWaitForCompletion(volumeName, (currentBrickRowCount - 2));
		if (result) {
			storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] two bricks removed.");
		} else {
			storageSahiTasks._logger.log(Level.WARNING, "Volume [" + volumeMap.getVolumeName() + "] failed to remove two bricks!");
			return false;
		}
		
		return true;
	}

	/**
	 * @return
	 */
	private ElementStub getVolumeBrickRow(int rowNum) {
		return storageSahiTasks.div(String.format("/%s/[%s]", GuiTables.BRICK_TABLE_REFERENCE, rowNum)).near(NEAR_REF_BRICKS_TABLE);
	}

	/**
	 * @return
	 */
	private ElementStub clickVolumeBrickRow( final ElementStub row) {
		WaitUtil.waitUntil(new Predicate<Integer>() { public boolean apply(Integer a){ return row.isVisible(); } }, 10);
		Assert.assertTrue(row.isVisible());		
		row.click();
		return row;
	}

	/**
	 * @param row
	 */
	private void validateBrickRowIsSelected(final ElementStub row) {
		String rowBackgroundColor = getVolumeBrickRowColor(row);
		Assert.assertEquals(rowBackgroundColor, LAVENDER);
	}
	
	private boolean waitUntilTableItemIsSelected(final ElementStub tableitem) {
		return WaitUtil.waitUntil(new Predicate<Integer>() {
			public boolean apply(Integer attepmt){
				return getVolumeBrickRowColor(tableitem).equals(LAVENDER);
			}
		}, 10, "table row is selected");
	}

	/**
	 * @param row
	 * @return
	 */
	private String getVolumeBrickRowColor(final ElementStub row) {
		return JQuery.toJQuery(new JsGeneric(row.toString())).addCall("closest", "tr").addCall("css", "background-color").fetch(storageSahiTasks);
	}
	
	/*
	 * Expectation is that brick row(s) are already highlighted (selected).
	 */
	private boolean removeBricksAndWaitForCompletion(String volumeName, int expectedBrickRowCount) {
		int wait = 1000;
		int retryCount = 40;
		
		final GwtButton removeButton = new GwtButton(new jQueryPagePanels().getMainTabSubPanel().find(".gwt-ToggleButton:contains(Remove)"), storageSahiTasks);
		assertTrue(removeButton.getElementStub().isVisible(), "Remove bricks button is not visible!");
		WaitUtil.waitUntil(new Predicate<Integer>() {
			
			@Override
			public boolean apply(Integer arg0) {
				storageSahiTasks._logger.log(Level.INFO, "removeBrick button is disabled? " + removeButton.isDisabled());
				return !removeButton.isDisabled();
			}
		}, NUM_ATTEMPTS);		
		removeButton.getElementStub().click();
		
		assertTrue(storageSahiTasks.div(BRICK_REMOVAL_CONFIRMATION).isVisible(), "Remove Bricks confirmation is not visible!");
		new RemoveBricksDialog(storageSahiTasks).getCheckbox().getElementStub().uncheck();
		storageSahiTasks.closePopup("OK");
		
		for (int x = 0; x < retryCount; x++) {
			storageSahiTasks.clickRefresh("Volume");
			LinkedList<HashMap<String, String>> bricksTable = GuiTables.getBricksTable(storageSahiTasks, NEAR_REF_BRICKS_TABLE);
			String str = String.format("Volume [%s]: expected number bricks [%s], actual number of bricks [%s]", volumeName, expectedBrickRowCount, bricksTable.size());
			storageSahiTasks._logger.log(Level.INFO, str);
			if(bricksTable.size() == expectedBrickRowCount) {
				break;
			} else {
				storageSahiTasks.wait(wait, retryCount, x);
				storageSahiTasks.clickRefresh("Volume");
			}
		}
		
		if(GuiTables.getBricksTable(storageSahiTasks, NEAR_REF_BRICKS_TABLE).size() != expectedBrickRowCount) {
			storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeName + "] brick(s) failed to remove!");
			return false;
		}
		
		return true;
	}
	
	/*
	 * Remove a Volume which is in "UP" state - negative
	 */
	public boolean removeVolumeNotStopped(VolumeMap volumeMap) {
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		
		storageSahiTasks.clickRefresh("Volume");
		
		VolumeTable table = new VolumeTable(storageSahiTasks);
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(table.getElementStub()), 10), "volume table is visible");
		
		if (table.isStatusDown(table.getFirstRowIndexThatContainsText(volumeMap.getVolumeName()))) {
			storageSahiTasks._logger.log(Level.WARNING, "Volume ["+volumeMap.getVolumeName()+"] is already in Down state!");
			return false;
		}
		
		storageSahiTasks.div("MainTabVolumeView_table_Remove").click();  // Click Remove button
		if(storageSahiTasks.div("/" + "Are you sure you want to remove the following Volume(s)??" + "/").exists()) {
			storageSahiTasks. _logger.log(Level.WARNING, "Volume ["+volumeMap.getVolumeName()+"] Remove Volume pop-up unexpectedly appeared!");
			storageSahiTasks.div("Cancel").click();
			return false;
		} else {
			storageSahiTasks._logger.log(Level.FINE, "Server ["+volumeMap.getVolumeName()+"] Remove Volume pop-up did not appear!");
		}
		return true;
	}
	
	public boolean validateVolumeGeneralTab(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException {
		String volumeName = volumeMap.getVolumeName();
		String cellReference = GuiTables.VOLUME_TABLE_REFERENCE + "1";
		
		storageSahiTasks.selectPage(volumeMap.getResourceLocation());
				
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.link("General").click();
		
		String actualVolumeName =  storageSahiTasks.textbox(0).near(storageSahiTasks.div("Name:")).getValue();
		Assert.assertEquals(actualVolumeName, volumeMap.getVolumeName(), "Volume [" + volumeName + "] name not as expected!");
		
		String actualVolumeType =  storageSahiTasks.textbox(0).near(storageSahiTasks.div("Volume Type:")).getValue();
		Assert.assertEquals(actualVolumeType.toLowerCase(), volumeMap.getVolumeType().toLowerCase(), "Volume [" + volumeName + "] type not as expected!"); 
		String actualVolumeID = storageSahiTasks.textbox(0).near(storageSahiTasks.div("Volume ID:")).getValue();
		Sshable server = volumeMap.getConfiguredServers().get(0);
		CliVolume volume = storageCliTasks.getVolumeInfo(volumeName, server);
		String expectedVolumeID = volume.getVolumeId();
		Assert.assertEquals(actualVolumeID, expectedVolumeID, "Volume [" + volumeName + "] ID not as expected");

		int rowNumber = GuiTables.getRowNumber(storageSahiTasks, cellReference, volumeName);
		Assert.assertTrue(rowNumber >= 0,"Volume [" + volumeMap.getVolumeName() + "] failed to find row.");
		
		String expectedBricks = GuiTables.getVolumesTable(storageSahiTasks).get(rowNumber).get(GuiTables.NUMBER_OF_BRICKS);
		int expectedNumberOfBricks = Integer.parseInt(expectedBricks);
		int actualNumberOfBricks = Integer.parseInt(storageSahiTasks.textbox(0).near(storageSahiTasks.div("Number of Bricks:")).getValue());
		
		Assert.assertEquals(actualNumberOfBricks, expectedNumberOfBricks,"Volume [" + volumeMap.getVolumeName() + "] incorrect number of bricks!");
		
		return true;
	}
	
	public boolean validateOptimizeForVirtStore(VolumeMap volumeMap) {
		String optionUID = String.valueOf(VolumeMap.optionType.STORAGE_OWNER_UID);
		String optionGID = String.valueOf(VolumeMap.optionType.STORAGE_OWNER_GID);
		int wait = 1000;
		int retryCount = 60;
		
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		
		storageSahiTasks.div(volumeMap.getVolumeName()).click(); // Highlight the desired volume.
		
		storageSahiTasks.link("Volume Options").click();
		
		if (storageSahiTasks.div(optionUID).exists() || storageSahiTasks.div(optionGID).exists()) {
			storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] Option [Storage User ID] is already present, nothing to do!");
			return true;
		}
		
		storageSahiTasks.div("MainTabVolumeView_table_OptimizeForVirtStore").click();
		
		for (int x = 0; x < retryCount; x++) {
			if (storageSahiTasks.div(optionUID).exists() && storageSahiTasks.div(optionGID).exists()) {
				break;
			} else {
				storageSahiTasks.wait(wait, retryCount, x);
			}
		}
		
		if (!storageSahiTasks.div(optionUID).exists() || !storageSahiTasks.div(optionGID).exists()) {
			storageSahiTasks._logger.log(Level.WARNING, "Volume [" + volumeMap.getVolumeName() + "] Optimize For Virt Store did not complete!");
			return false;
		}
		
		storageSahiTasks._logger.log(Level.INFO, "Volume [" + volumeMap.getVolumeName() + "] Optimize ForVirt Store complete.");
		
		return true;
	}
	
	public boolean isVolumeExists(VolumeMap volumeMap){
		return isVolumePresent(volumeMap);
	}
	
	/**
	 * TODO May need to change. Warnings messages have not been added to console yet.
	 * @param warningMsg
	 * @return
	 */
	private boolean isWarningPresent(WarningMessage warningMsg) {
		return storageSahiTasks.div(String.format("/%s/", warningMsg.toString())).exists();
	}
	
	
	
	public boolean validateBrickSummaryTab(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException {
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.link("Bricks").click();
		
		
		ElementStub table = storageSahiTasks.table("[0]").near(storageSahiTasks.tableHeader("Brick Directory[1]"));
		LinkedList<HashMap<String, String>> tableData = GuiTables.getBricksTable(storageSahiTasks, table);
		
		verifyBrickSummaryTableContainsAllExpectedBricksFromConfiguration( volumeMap, tableData);
		
		verifyAllBricksHaveUpStatus(tableData);
		
		return true;
	}

	private void verifyBrickSummaryTableContainsAllExpectedBricksFromConfiguration( VolumeMap volumeMap, final LinkedList<HashMap<String, String>> tableData) throws TestEnvironmentConfigException, FileNotFoundException, IOException, JAXBException {
		volumeMap.forEachBrick(new EachBrickAction(){
			public void perform(final Server server, String brickSetName, final Brick brick){
				Collection<HashMap<String, String>> matchingItems = Collections2.filter(tableData, new Predicate<HashMap<String,String>>(){
					public boolean apply(HashMap<String,String> input){
						return input.get(GuiTables.BRICK_DIRECTORY).equals(brick.getLocation()) && input.get(GuiTables.BRICK_SERVER).equals(server.getHostname());
					}
				});
				Assert.assertTrue(matchingItems.size() > 0, String.format("could not find a matching row for server:brick %s:%s in brick summary table", server.getName(), brick.getLocation()));
			}
		});
	}
	
	private void verifyAllBricksHaveUpStatus(final LinkedList<HashMap<String, String>> tableData) {
		BrickTable table = new BrickTable(storageSahiTasks);
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(table.getElementStub()), 10), "brick table is visible");
		for(HashMap<String,String> row : tableData){
			String actualServer = row.get(GuiTables.BRICK_SERVER);
			String actualbrickDirectory = row.get(GuiTables.BRICK_DIRECTORY);
			boolean isUp = table.isStatusUp(table.getFirstRowIndexThatContainsText(actualbrickDirectory));
			Assert.assertTrue(isUp, String.format("server:brick (%s:%s) directory was not listed as up in brick tab summary", actualServer, actualbrickDirectory));
		}
	}
	
	public int getNumberOfBricks(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		HashMap<String, String> serversBricks = storageSahiTasks.getListMapFromString(volumeMap.getServers());
		List<Server> servers = storageSahiTasks.getServers(serversBricks.keySet().toString().replaceAll("[\\[\\]]", ""));
		int numberOfBricks = 0;
		for(Server server: servers){
			String[] bricksArray = serversBricks.get(server.getName()).split(",");
			for(String BrickName : bricksArray){
				for(Brick brick: server.getBricks(BrickName.trim())){
					numberOfBricks++;
				}
			}
		}
		return numberOfBricks;
	}
	
	public boolean addAndRemoveVolumeOption(VolumeMap volume) {
		String volumeOptionFormat = volume.getVolumeOptionKey() + ":" + volume.getVolumeOptionValue();
		Assert.assertTrue(addVolumeOption(volume), "Volume [" + volume.getVolumeName() + "] option[" + volumeOptionFormat + "] set successfully");
		Assert.assertTrue(resetVolumeOption(volume), "Volume ["+volume.getVolumeName()+"] option[" + volumeOptionFormat + "] reset successfully");
		return true;
		
	}
	
	
	public boolean addVolumeToEmptyClusterPromptsError(VolumeMap volumeMap) {
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		storageSahiTasks.div("MainTabVolumeView_table_Create_Volume").click();
		storageSahiTasks.select("VolumePopupView_cluster").choose(volumeMap.getClusterName());
		storageSahiTasks.textbox("VolumePopupView_nameEditor").setValue(volumeMap.getVolumeName());
		
		verifyBrickButtonIsDisabled();
		Assert.assertTrue(storageSahiTasks.div("/No host found in 'UP' state.*/").exists());
		
		storageSahiTasks.div("VolumePopupView_Cancel").click();
		return true;
	}
	
	
	private void verifyBrickButtonIsDisabled() {
		//could have been done easier with jQuery or javascript...just check that the input has attribute disabled set.
		storageSahiTasks.div("VolumePopupView_addBricksButton").click();
		Assert.assertFalse(storageSahiTasks.select("AddBrickPopupView_serverEditor").isVisible(), "brick button was not disabled");
	}
	
	public boolean validateAllVolumeOptionsHaveDescriptions(VolumeMap volumeMap){
		//Navigate
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())) {
			assertFalse(true, "did not navigate successfully");
		}
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.link("Volume Options").click();
		storageSahiTasks.div("Add").click();
		
		//Test
		SelectElement optionKeySelect = new SelectElement(storageSahiTasks, storageSahiTasks.select("VolumeParameterPopupView_keyListBox"));
		Collection<String> optionKeys = optionKeySelect.getOptionInnerTexts();
		try{
			for(String key : optionKeys){
				optionKeySelect.getElement().choose(key);
				String description = storageSahiTasks.textarea("[0]").near(storageSahiTasks.label("Description")).getValue();
				assertNotNull(description, String.format("description was null for volume option key, %s", key));
				assertFalse(description.isEmpty(), String.format("description was empty for volume option key, %s", key));
			}
		}finally{
			storageSahiTasks.closePopup("Cancel");
		}
		return true;
	}
	
    
    public boolean doesServerHaveVolumes(ServerMap server) {
    	Assert.assertTrue(storageSahiTasks.selectPage("System->Volumes"));
    	storageSahiTasks.clickRefresh("Volume");
    	
    	LinkedList<HashMap<String, String>> volumeTable = GuiTables.getVolumesTable(storageSahiTasks);
    	if (volumeTable.size() < 1) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
}
