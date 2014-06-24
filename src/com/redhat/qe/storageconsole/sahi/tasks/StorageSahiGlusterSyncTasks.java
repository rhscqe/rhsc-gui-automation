/**
 *
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;

import com.google.common.base.Predicate;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.elements.BrickTable;
import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.elements.VolumeTable;
import com.redhat.qe.storageconsole.helpers.elements.tab.VolumeOptionSubTab;
import com.redhat.qe.storageconsole.helpers.elements.tab.VolumeTab;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap.EachBrickAction;
import com.redhat.qe.storageconsole.mappper.VolumeOptionsMap;
import com.redhat.qe.storageconsole.te.Brick;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.Sshable;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author shruti
 * Dec 24, 2012
 */
public class StorageSahiGlusterSyncTasks {
	StorageSahiVolumeTasks storageSahiVolumeTasks = null;
	StorageCLITasks storageCLITasks = new StorageCLITasks();
	StorageSahiEventTasks storageSahiEventTasks = null;
	StorageBrowser storageSahiTasks = null;

	public StorageSahiGlusterSyncTasks(StorageBrowser tasks){
		storageSahiTasks = tasks;
		storageSahiVolumeTasks = new StorageSahiVolumeTasks(storageSahiTasks);
		storageSahiEventTasks = new StorageSahiEventTasks(storageSahiTasks);
	}

	/*
	 * Creates volume from Gluster CLI and verifies if the change is shown by the Console
	 */
	public boolean syncVolumeCreate(final VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		final int retryCount = 25;
		Sshable serverToRunCommandOn = volumeMap.getConfiguredServers().get(0);
		Assert.assertTrue(storageCLITasks.glusterCreateVolume(volumeMap, serverToRunCommandOn)
				, "Unable to create volume["+volumeMap.getVolumeName()+"] from gluster CLI on server["+serverToRunCommandOn+"]!");
		// Verify if the GUI displays all volume details correct
		storageSahiTasks.selectPage(volumeMap.getResourceLocation());
		
		WaitUtil.waitUntil(new Predicate<Integer>(){
			public boolean apply(Integer attempt){
				storageSahiTasks._logger.info(String.format("waiting for volume to appear on gui attempt %s/%s", attempt, retryCount));
				storageSahiTasks.clickRefresh("Volume");
				return storageSahiTasks.div(volumeMap.getVolumeName()).exists();
			}
		}, 25);
		
		Assert.assertTrue(storageSahiTasks.div(volumeMap.getVolumeName()).exists(),  "Volume ["+volumeMap.getVolumeName()+"] is not available on the list!");
		checkBrickDetails(volumeMap);		
		
		storageSahiTasks.div("Volume Options").click();
//		Assert.assertTrue(GuiTables.getVolumesOptionsTable(storageSahiTasks, storageSahiVolumeTasks.NEAR_REF_VOLUME_OPTIONS_TABLE).isEmpty()
//                               , "Volume Options table for volume["+ volumeMap.getVolumeName() +"] should be empty!");
		// Before 3.0 volume-options table was expected to be empty, but since 3.0, 4 options are set by default.
		int volumeOptionsTableSize = GuiTables.getVolumesOptionsTable(storageSahiTasks, storageSahiVolumeTasks.NEAR_REF_VOLUME_OPTIONS_TABLE).size();
        if(volumeOptionsTableSize > 4){
            storageSahiTasks._logger.log(Level.SEVERE,
                    "Volume Options table for volume["+ volumeMap.getVolumeName() +"] should have only user-set options!");
            return false;
        }
		Assert.assertTrue(storageSahiTasks.div("/Detected new volume "+volumeMap.getVolumeName()+"/").exists()
				, "Events message did not appear!");
		return true;
	}

	/**
	 * Checks the details displayed in the row corresponding to a volume
	 * @throws TestEnvironmentConfigException
	 * @throws JAXBException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
//	private boolean checkVolumeDetails(VolumeMap volumeMap) throws TestEnvironmentConfigException, FileNotFoundException, IOException, JAXBException {
//		HashMap<String, String> volumeRow = GuiTables.getVolume(storageSahiTasks, volumeMap.getVolumeName());
//		int numberOfBricks = storageSahiVolumeTasks.getNumberOfBricks(volumeMap);
//		if(!volumeRow.get(GuiTables.VOLUME_TYPE).equals(volumeMap.getVolumeType())){
//			storageSahiTasks._logger.log(Level.SEVERE, "Volume["+volumeMap.getVolumeName()+"] type incorrectly displayed as "+volumeRow.get(GuiTables.VOLUME_TYPE));
//			return false;
//		}else if(Integer.parseInt(volumeRow.get(GuiTables.NUMBER_OF_BRICKS)) != numberOfBricks){
//			storageSahiTasks._logger.log(Level.SEVERE, "Volume["+volumeMap.getVolumeName()+"] number_of_bricks incorrectly displayed as "+volumeRow.get(GuiTables.NUMBER_OF_BRICKS));
//			return false;
//		}else if(!volumeRow.get(GuiTables.TRANSPORT_TYPES).equals("TCP")){
//			storageSahiTasks._logger.log(Level.SEVERE, "Volume["+volumeMap.getVolumeName()+"] transport_type incorrectly displayed as "+volumeRow.get(GuiTables.TRANSPORT_TYPES));
//			return false;
//		}else if(!volumeRow.get(GuiTables.STATUS).equals("Down")){
//			storageSahiTasks._logger.log(Level.SEVERE, "Volume["+volumeMap.getVolumeName()+"] status incorrectly displayed as "+volumeRow.get(GuiTables.STATUS));
//			return false;
//		}
//		return true;
//	}

	private boolean checkBrickDetails(final VolumeMap volumeMap) throws TestEnvironmentConfigException, FileNotFoundException, IOException, JAXBException  {
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		storageSahiTasks.clickRefresh("Volume");
		storageSahiTasks.div("Bricks").click();
		final BrickTable brickTable = new BrickTable(storageSahiTasks);
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(brickTable.getElementStub()), 10), "brick table is visible");
		final LinkedList<HashMap<String, String>> bricksTable = GuiTables.getBricksTable(storageSahiTasks, storageSahiVolumeTasks.NEAR_REF_BRICKS_TABLE);//Read table from Console

		int numberOfBricks = storageSahiVolumeTasks.getNumberOfBricks(volumeMap);
		Assert.assertEquals(bricksTable.size(), numberOfBricks,  "Number of bricks displayed is incorrect!");
		
		volumeMap.forEachBrick(new EachBrickAction() {
			
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				final BrickTable brickTable = new BrickTable(storageSahiTasks);
					final HashMap<String, String> brickToCompare = new HashMap<String, String>();
					brickToCompare.put(GuiTables.BRICK_SERVER, server.getHostname());
					brickToCompare.put(GuiTables.BRICK_DIRECTORY, brick.getLocation());
					storageSahiTasks._logger.log(Level.FINE, "Brick location : " + brick.getLocation());
					storageSahiTasks._logger.log(Level.FINE, "Brick status : Down");
					storageSahiTasks._logger.log(Level.FINE, "---------------");
					Assert.assertTrue(brickTable.isStatusDown(brickTable.getRowIdx(server.getHostname(), brick.getLocation())));
					Assert.assertTrue(bricksTable.contains(brickToCompare), "Brick Details ["+ server.getHostname()+":"+brick.getLocation()+"] for volume ["+ volumeMap.getVolumeName() +"] not correct!");
			}
		});
		return true;
	}
	
	/**
	 * Checks details displayed by the bricks sub-tab
	 * @throws TestEnvironmentConfigException
	 * @throws JAXBException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *
	 */
//	private boolean checkBrickDetails(VolumeMap volumeMap, List<Server> servers, HashMap<String, String> serversBricks) throws TestEnvironmentConfigException, FileNotFoundException, IOException, JAXBException  {
//		storageSahiTasks.div(volumeMap.getVolumeName()).click();
//		storageSahiTasks.div("Bricks").click();
//		LinkedList<HashMap<String, String>> bricksTable = GuiTables.getBricksTable(storageSahiTasks, storageSahiVolumeTasks.NEAR_REF_BRICKS_TABLE);//Read table from Console
//		final BrickTable table = new BrickTable(storageSahiTasks);
//		HashMap<String, String> brickToCompare = new HashMap<String, String>();
//		int numberOfBricks = storageSahiVolumeTasks.getNumberOfBricks(volumeMap);
//		if(bricksTable.size() != numberOfBricks){
//			storageSahiTasks._logger.log(Level.SEVERE, "Number of bricks displayed is incorrect!");
//			return false;
//		}
//		for(Server server: servers){
//			String[] bricksArray = serversBricks.get(server.getName()).split(",");
//			for(String BrickName : bricksArray){
//				for(Brick brick: server.getBricks(BrickName.trim())){
//					brickToCompare.put(GuiTables.BRICK_SERVER, server.getHostname());
//					brickToCompare.put(GuiTables.BRICK_DIRECTORY, brick.getLocation());
//					storageSahiTasks._logger.log(Level.FINE, "Brick location : " + brick.getLocation());
//					brickToCompare.put(GuiTables.BRICK_STATUS, "Down");
//					storageSahiTasks._logger.log(Level.FINE, "Brick status : Down");
//					storageSahiTasks._logger.log(Level.FINE, "---------------");
//					if(!bricksTable.contains(brickToCompare)){
//						storageSahiTasks._logger.log(Level.SEVERE, "Brick Details ["+ server.getHostname()+":"+brick.getLocation()+"] for volume ["+ volumeMap.getVolumeName() +"] not correct!");
//						return false;
//					}
//				}
//			}
//		}
//		return true;
//	}

	/**
	 * Deletes volume from Gluster CLI and checks if the Console displays the change.
	 * @throws JAXBException
	 * @throws IOException
	 * @throws TestEnvironmentConfigException
	 * @throws FileNotFoundException
	 */
	public boolean syncVolumeDelete(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException {
		int wait = 500;
		int retryCount = 25;
		HashMap<String, String> serversBricks = storageSahiTasks.getListMapFromString(volumeMap.getServers());
		List<Server> servers = storageSahiTasks.getServers(serversBricks.keySet().toString().replaceAll("[\\[\\]]", ""));
		if(!storageCLITasks.glusterDeleteVolume(volumeMap, servers.get(0))){ //Deletes volume from GlusterCLI
			storageSahiTasks._logger.log(Level.SEVERE, "Unable to delete volume["+volumeMap.getVolumeName()+"] from gluster CLI on server["+servers.get(0).getHostname()+"]!");
			return false;
		}
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		for (int x = 0; x < retryCount; x++) {
			if(!storageSahiTasks.div(volumeMap.getVolumeName()).exists()){
				break;
			} else {
				storageSahiTasks.clickRefresh("Volume");
				storageSahiTasks.wait(wait, retryCount, x);
			}
		}
		if(storageSahiTasks.div(volumeMap.getVolumeName()).exists()){
			storageSahiTasks._logger.log(Level.SEVERE, "Volume ["+volumeMap.getVolumeName()+"] is available on the list!");
			return false;
		}
		for (int x = 0; x < retryCount; x++) {
			if(storageSahiTasks.div("/"+volumeMap.getVolumeName()+".*deleted/").exists()){
				if(x == retryCount - 1){
					storageSahiTasks._logger.log(Level.SEVERE, "Events message did not appear!");
					return false;
				}
				break;
			} else {
				storageSahiTasks.clickRefresh("Volume");
				storageSahiTasks.wait(wait, retryCount, x);
			}
		}
		return true;
	}

	/**
	 * @param volumeMap
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws TestEnvironmentConfigException
	 * @throws FileNotFoundException
	 */
	public boolean syncVolumeStart(final VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException {
		final int retryCount = 60;
		
		storageSahiTasks.selectPage(volumeMap.getResourceLocation());
		storageSahiTasks.clickRefresh("Volume");
		HashMap<String, String> row = GuiTables.getVolume(storageSahiTasks, volumeMap.getVolumeName());
		
		VolumeTable table = new VolumeTable(storageSahiTasks);
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(table.getElementStub()), 10), "volume table is visible");
		Assert.assertFalse(table.isStatusUp(table.getFirstRowIndexThatContainsText(volumeMap.getVolumeName())), "Volume ["+volumeMap.getVolumeName()+"] is already in running state, Nothing to do!");
		
		Sshable server = volumeMap.getConfiguredServers().get(0);
		Assert.assertTrue(storageCLITasks.glusterStartVolume(volumeMap, server), 
				"Unable to start volume["+volumeMap.getVolumeName()+"] from gluster CLI on server["+server.getHostname()+"]!");
		
		boolean isVolumeUp = WaitUtil.waitUntil(new Predicate<Integer>(){
			public boolean apply(Integer attempt){
				storageSahiTasks._logger.info(String.format("waiting for volume up attempt %s/%s", attempt, retryCount));
				storageSahiTasks.clickRefresh("Volume");
				VolumeTable volumeTable = new VolumeTable(storageSahiTasks);
				return volumeTable.isStatusUp(volumeTable.getFirstRowIndexThatContainsText(volumeMap.getVolumeName()));
			}
			
		}, retryCount);
		
		Assert.assertTrue(isVolumeUp, "Volume ["+volumeMap.getVolumeName()+"] is not started!");
		
		boolean isEventDisplayed = WaitUtil.waitUntil(new Predicate<Integer>(){
			public boolean apply(Integer attempt){
				storageSahiTasks._logger.info(String.format("waiting for event to display %s/%s", attempt, retryCount));
				storageSahiTasks.clickRefresh("Volume");
				return storageSahiTasks.div("/"+volumeMap.getVolumeName()+".*was started/")
						.near(storageSahiTasks.div("Last Message:")).exists();
			}
			
		}, retryCount);
		Assert.assertTrue(isEventDisplayed, "Events \"Start\" message did not appear for volume \"" + volumeMap.getVolumeName() + "\"!");
		return true;
	}

	/**
	 * @param volumeMap
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws TestEnvironmentConfigException
	 * @throws FileNotFoundException
	 */
	public boolean syncVolumeStop(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException {
		int wait = 500;
		int retryCount = 25;
		HashMap<String, String> serversBricks = storageSahiTasks.getListMapFromString(volumeMap.getServers());
		List<Server> servers = storageSahiTasks.getServers(serversBricks.keySet().toString().replaceAll("[\\[\\]]", ""));
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		
		VolumeTable table = new VolumeTable(storageSahiTasks);
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(table.getElementStub()), 10), "volume table is visible");
		if (table.isStatusDown(table.getFirstRowIndexThatContainsText(volumeMap.getVolumeName()))) {
			storageSahiTasks._logger.log(Level.INFO, "Volume ["+volumeMap.getVolumeName()+"] is already in stopped state, Nothing to do!");
			return true;
		}

		if(!storageCLITasks.glusterStopVolume(volumeMap, servers.get(0))){ //Deletes volume from GlusterCLI
			storageSahiTasks._logger.log(Level.SEVERE, "Unable to stop volume["+volumeMap.getVolumeName()+"] from gluster CLI on server["+servers.get(0).getHostname()+"]!");
			return false;
		}
		
		storageSahiVolumeTasks.waitForVolumeToBeDown(volumeMap);		
		
		for (int x = 0; x < retryCount; x++) {
			if(storageSahiTasks.div("/"+volumeMap.getVolumeName()+".*was stopped/").exists()){
				if(x == retryCount - 1){
					storageSahiTasks._logger.log(Level.SEVERE, "Events message did not appear!");
					return false;
				}
				break;
			} else {
				storageSahiTasks.clickRefresh("Volume");
				storageSahiTasks.wait(wait, retryCount, x);
			}
		}
		return true;
	}

	public boolean syncVolumeOptionAdd(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		int wait = 500;
		int retryCount = 10;
		int i = 0;
		boolean isOptionSet = false;
		List<VolumeOptionsMap> volumeOptions = volumeMap.getVolumeOptions();
		HashMap<String, String> serversBricks = storageSahiTasks.getListMapFromString(volumeMap.getServers());
		List<Server> servers = storageSahiTasks.getServers(serversBricks.keySet().toString().replaceAll("[\\[\\]]", ""));
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		while(!storageSahiTasks.link("Volume Options").isVisible() && i < retryCount){
			storageSahiTasks.div(volumeMap.getVolumeName()).click();
			i++;
		}
		storageSahiTasks.link("Volume Options").click();
		for(VolumeOptionsMap option : volumeOptions){
			String optionName = option.getOptionName();
			String optionValue = option.getOptionValue();
			storageSahiTasks.clickRefresh("Volume");
			if(storageSahiTasks.div(optionName).exists()){
				storageSahiTasks._logger.log(Level.INFO, "Volume option " + optionName + " already set on volume " + volumeMap.getVolumeName() + ". Nothing to do!");
				continue;
			}
			if(!storageCLITasks.glusterVolumeOptionSet(volumeMap.getVolumeName(), optionName, optionValue, servers.get(0))){
				storageSahiTasks._logger.log(Level.SEVERE, "Unable to set volume option " + optionName + " with value " + optionValue + " on volume " + volumeMap.getVolumeName() + " from glusterCLI !");
				return false;
			}
			for (int x = 0; x < retryCount; x++) {
				if(storageSahiTasks.div(optionName).exists()){
					isOptionSet = true;
					storageSahiTasks._logger.log(Level.FINE, "Option Set!");
					break;
				} else {
					storageSahiTasks.clickRefresh("Volume");
					storageSahiTasks.wait(wait, retryCount, x);
				}
			}
			if(!isOptionSet){
				storageSahiTasks._logger.log(Level.SEVERE, "Volume option " + optionName + " on volume " + volumeMap.getVolumeName() + " failed to set!");
				return false;
			}
		}
		//validate Events log message for each option reset.
		storageSahiTasks.div("Events").click();
		storageSahiTasks.waitFor(3000);
		String message = null;
		for(VolumeOptionsMap option : volumeOptions){
			String optionName = option.getOptionName();
			String optionValue = option.getOptionValue();
			message = "/[Oo]ption "+optionName+".*on [vV]olume "+volumeMap.getVolumeName()+"/";
			if (storageSahiTasks.div(message).near(storageSahiEventTasks.NEAR_REF_EVENTS_TABLE).exists()){
				storageSahiTasks._logger.log(Level.INFO, "Events message for option : " + optionName + " found.");
			} else {
				storageSahiTasks._logger.log(Level.INFO, "Events message for option : " + optionName + " not found.");
				return false;
			}
		}
		return true;
	}

	public boolean syncVolumeOptionEdit(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		int wait = 500;
		int retryCount = 10;
		boolean isOptionSet = false;
		int i = 0;
		List<VolumeOptionsMap> volumeOptions = volumeMap.getVolumeOptions();
		HashMap<String, String> optionKeyValueMap = null;
		HashMap<String, String> serversBricks = storageSahiTasks.getListMapFromString(volumeMap.getServers());
		List<Server> servers = storageSahiTasks.getServers(serversBricks.keySet().toString().replaceAll("[\\[\\]]", ""));
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		while(!storageSahiTasks.link("Volume Options").isVisible() && i < retryCount){
			storageSahiTasks.div(volumeMap.getVolumeName()).click();
			i++;
		}
		storageSahiTasks.link("Volume Options").click();
		for(VolumeOptionsMap option: volumeOptions){
			String optionName = option.getOptionName();
			String editOptionValue = option.getEditOptionValue();
			storageSahiTasks.clickRefresh("Volume");
			if(!storageSahiTasks.div(optionName).exists()){
				storageSahiTasks._logger.log(Level.INFO, "Volume option " + optionName + " not present on volume " + volumeMap.getVolumeName() + ". Nothing to do!");
				return true;
			}
			storageSahiTasks._logger.log(Level.FINE, "editOptionValue " + editOptionValue);
			if(!storageCLITasks.glusterVolumeOptionSet(volumeMap.getVolumeName(), optionName, editOptionValue, servers.get(0))){
				storageSahiTasks._logger.log(Level.SEVERE, "Unable to set volume option " + optionName + " with value " + editOptionValue + " on volume " + volumeMap.getVolumeName() + " from glusterCLI !");
				return false;
			}
			for (int x = 0; x < retryCount; x++) {
				if(isOptionSet){
					break;
				}
				optionKeyValueMap = GuiTables.getVolumeOption(storageSahiTasks, optionName, storageSahiVolumeTasks.NEAR_REF_VOLUME_OPTIONS_TABLE);
				if (optionKeyValueMap != null && optionKeyValueMap.get(GuiTables.VOLUME_OPTION_VALUE).equals(editOptionValue)) {
					isOptionSet = true;
					break;
				} else {
					storageSahiTasks.clickRefresh("Volume");
					storageSahiTasks.wait(wait, retryCount, x);
				}
			}
			if(!isOptionSet){
				storageSahiTasks._logger.log(Level.SEVERE, "Volume option " + optionName + " on volume " + volumeMap.getVolumeName() + " failed to edit!");
				return false;
			}
		}
		//validate Events log message for each option reset.
		storageSahiTasks.div("Events").click();
		storageSahiTasks.waitFor(3000);
		String message = null;
		for(VolumeOptionsMap option : volumeOptions){
			String optionName = option.getOptionName();
			String optionValue = option.getOptionValue();
			message = String.format("/[oO]ption %s.*from %s.*to %s.*%s/", optionName, optionValue, option.getEditOptionValue(), volumeMap.getVolumeName());
			if (storageSahiTasks.div(message).near(storageSahiEventTasks.NEAR_REF_EVENTS_TABLE).exists()){
				storageSahiTasks._logger.log(Level.INFO, "Events message for option : " + optionName + " found.");
			} else {
				storageSahiTasks._logger.log(Level.INFO, "Events message for option : " + optionName + " not found.");
				return false;
			}
		}
		return true;
	}

	/**
	 * @param volumeMap
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws TestEnvironmentConfigException
	 * @throws FileNotFoundException
	 */
	public boolean syncVolumeOptionReset(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException {
		int wait = 500;
		int retryCount = 10;
		boolean isOptionReset = false;
		int i = 0;
		List<VolumeOptionsMap> volumeOptions = volumeMap.getVolumeOptions();
		HashMap<String, String> serversBricks = storageSahiTasks.getListMapFromString(volumeMap.getServers());
		List<Server> servers = storageSahiTasks.getServers(serversBricks.keySet().toString().replaceAll("[\\[\\]]", ""));
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		while(!storageSahiTasks.link("Volume Options").isVisible() && i < retryCount){
			storageSahiTasks.div(volumeMap.getVolumeName()).click();
			i++;
		}
		storageSahiTasks.link("Volume Options").click();
		for(VolumeOptionsMap option : volumeOptions){
			String optionName = option.getOptionName();
			storageSahiTasks.clickRefresh("Volume");
			storageSahiTasks._logger.log(Level.FINE, "Going to check if option is already reset.");
			if(!storageSahiTasks.div(optionName).exists()){
				storageSahiTasks._logger.log(Level.INFO, "Volume option " + optionName + " not set on volume " + volumeMap.getVolumeName() + ". Nothing to do!");
				return true;
			}
			if(!storageCLITasks.glusterVolumeOptionReset(volumeMap.getVolumeName(), optionName, servers.get(0))){
				storageSahiTasks._logger.log(Level.SEVERE, "Unable to reset volume option " + optionName + " on volume " + volumeMap.getVolumeName() + " from glusterCLI !");
				return false;
			}
			for (int x = 0; x < retryCount; x++) {
				if(!storageSahiTasks.div(optionName).exists()){
					storageSahiTasks._logger.log(Level.FINE, "Option Reset!");
					isOptionReset = true;
					break;
				} else {
					storageSahiTasks.clickRefresh("Volume");
					storageSahiTasks.wait(wait, retryCount, x);
				}
			}
			if(!isOptionReset){
				storageSahiTasks._logger.log(Level.SEVERE, "Volume option " + optionName + " on volume " + volumeMap.getVolumeName() + " failed to reset!");
				return false;
			}
		}
		//validate Events log message for each option reset.
		storageSahiTasks.div("Events").click();
		storageSahiTasks.waitFor(3000);
		String message = null;
		for(VolumeOptionsMap option : volumeOptions	){
			String optionName = option.getOptionName();
			message = "/[Oo]ption "+optionName+".*reset on [Vv]olume "+volumeMap.getVolumeName()+"/";
			if (storageSahiTasks.div(message).near(storageSahiEventTasks.NEAR_REF_EVENTS_TABLE).exists()){
				storageSahiTasks._logger.log(Level.INFO, "Events message for option : " + optionName + " found.");
			} else {
				storageSahiTasks._logger.log(Level.INFO, "Events message for option : " + optionName + " not found.");
				return false;
			}
		}
		return true;
	}

	public boolean syncVolumeOptionResetAll(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		storageSahiEventTasks = new StorageSahiEventTasks(storageSahiTasks);
		int wait = 500;
		int retryCount = 10;
		int i = 0;
		HashMap<String, String> serversBricks = storageSahiTasks.getListMapFromString(volumeMap.getServers());
		List<Server> servers = storageSahiTasks.getServers(serversBricks.keySet().toString().replaceAll("[\\[\\]]", ""));
		if(!storageSahiTasks.selectPage(volumeMap.getResourceLocation())){
			return false;
		}
		storageSahiTasks.div(volumeMap.getVolumeName()).click();
		while(!storageSahiTasks.link("Volume Options").isVisible()){
			storageSahiTasks.div(volumeMap.getVolumeName()).click();
		}
		storageSahiTasks.link("Volume Options").click();
		storageSahiTasks.clickRefresh("Volume");
		 
		
		VolumeOptionSubTab volumeOptionSubTab = new VolumeOptionSubTab(storageSahiTasks);
		Assert.assertTrue(volumeOptionSubTab.waitUntilArrived(), "volume options sub tab not avialable");
		if(volumeOptionSubTab.getTable().getData().isEmpty()){
			storageSahiTasks._logger.log(Level.INFO, "Volume Options table for volume["+ volumeMap.getVolumeName() +"] is empty. Nothing to do!");
			return true;
		}
		if(!storageCLITasks.glusterVolumeOptionResetAll(volumeMap.getVolumeName(), servers.get(0))){
			storageSahiTasks._logger.log(Level.SEVERE, "Unable to reset all volume options on volume " + volumeMap.getVolumeName() + " from glusterCLI !");
			return false;
		}
		
		Assert.assertTrue(volumeOptionSubTab.waitUntilArrived(), "volume options sub-tab not avialable");
		waitUntilVolumeOptionsTableIsEmpty(wait, retryCount);
		
		//validate Events log message for each option reset.
		storageSahiTasks.div("Events").click();
		storageSahiTasks.waitFor(3000);
		String message = null;
		for(HashMap<String, String> option : volumeOptionSubTab.getTable().getData()){
			String optionName = option.get(GuiTables.VOLUME_OPTION_KEY);
			message = "/[Oo]ption "+optionName+".*reset on [Vv]olume "+volumeMap.getVolumeName()+"/";
			if (storageSahiTasks.div(message).near(storageSahiEventTasks.NEAR_REF_EVENTS_TABLE).exists()){
				storageSahiTasks._logger.log(Level.INFO, "Events message for option : " + optionName + " found.");
			} else {
				storageSahiTasks._logger.log(Level.INFO, "Events message for option : " + optionName + " not found.");
				return false;
			}
		}
		return true;
	}

	/**
	 * @param wait
	 * @param retryCount
	 * @param areOptionsReset
	 * @param volumeTab
	 * @return
	 */
	private void waitUntilVolumeOptionsTableIsEmpty(int wait, int retryCount) {
		for (int x = 0; x < retryCount; x++) {//validate that the options table is empty
			if(new VolumeOptionSubTab(storageSahiTasks).getTable().getData().isEmpty()){
				storageSahiTasks._logger.log(Level.INFO, "Volume options table empty now.");
				break;
			} else {
				storageSahiTasks.clickRefresh("Volume");
				storageSahiTasks.wait(wait, retryCount, x);
			}
		}
	}


}
