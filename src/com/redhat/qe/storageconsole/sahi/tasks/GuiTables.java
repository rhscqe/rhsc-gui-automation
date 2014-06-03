/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.redhat.qe.storageconsole.helpers.elements.VolumeTable;

import net.sf.sahi.client.ElementStub;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 16, 2012
 */
public class GuiTables {
	protected static Logger _logger = Logger.getLogger(GuiTables.class.getName());

	/*
	 * Table Headers - Common
	 */
	public static final String NAME 						= "Name";
	public static final String CLUSTER 	= "Cluster";	
	public static final String DESCRIPTION 					= "Description";
	public static final String STATUS 						= "Status";

	
	/*
	 * Clusters Table Headers
	 */
	public static final String COMPATIBLITY_VERSION 		= "Compatiblity Version";
	public static final String DATA_CENTER					= "Data Center";
	public static final String CLUSTERS_TABLE_REFERENCE 	= "MainTabClusterView_table_content_col";

	
	/*
	 * Servers Table Headers
	 */
	public static final String HOST_IP 	= "Host/IP";
	public static final String MEMORY 	= "Memory";
	public static final String CPU 	= "CPU";
	public static final String NETWORK 	= "Network";
	public static final String SERVERS_TABLE_REFERENCE 	= "MainTabHostView_table_content_col";
	
	/*
	 * Volumes Table Headers
	 */
	public static final String VOLUME_TYPE 	= "Volume Type";
	public static final String NUMBER_OF_BRICKS = "Bricks";
	public static final String ACTIVITIES = "Activities";
	public static final String VOLUME_TABLE_REFERENCE 	= "MainTabVolumeView_table_content_col";

	/*
	 * General Arguments
	 */
	public static final String GENERAL_TABLE_REFERENCE = "gwt-uid-.*_col";
	
    /*
     * Volume Options Table Headers
     */
	public static final String VOLUME_OPTION_KEY = "Option Key";
	public static final String VOLUME_OPTION_VALUE = "Option Value";
	public static final String VOLUME_OPTION_TABLE_REFERENCE = "SubTabVolumeParameterView_table_content_col";
	
	/*
	 * Brick Sub-tab reference
	 */
	public static final String BRICK_SERVER = "Server";
	public static final String BRICK_DIRECTORY = "Brick Directory";
	public static final String BRICK_ACTIVITIES = "Activities";
	public static final String BRICK_TABLE_REFERENCE = "SubTabVolumeBrickView_table_content_col";
	
	/*
	 * Events tab reference
	 */
	public static final String EVENT_TIME = "Time";
	public static final String EVENT_MESSAGE = "Message";
	public static final String EVENT_ID = "EventsID";
	public static final String EVENT_USER = "User";
	public static final String EVENT_HOST = "Host";
	public static final String EVENT_CLUSTER = "Cluster";
	public static final String EVENT_VOLUME = "Gluster Volume";
	public static final String EVENT_CORRELATION_ID = "Correlation Id";
	public static final String EVENT_TABLE_REFERENCE = GENERAL_TABLE_REFERENCE;

    /*
     * Cluster Services Sub-tab reference
     */
    public static final String CLUSTER_SERVICES_HOST = "Host";
    public static final String CLUSTER_SERVICES_SERVICE = "Service";
    public static final String CLUSTER_SERVICES_STATUS = "Status";
    public static final String CLUSTER_SERVICES_PORT = "Port";
    public static final String CLUSTER_SERVICES_PROCESS_ID = "Process Id";
    public static final String CLUSTER_SERVICES_TABLE_REFERENCE = GENERAL_TABLE_REFERENCE;    
    /*
     * Add Brick Modal reference
     */
//    public static final String ADD_BRICK_CHECK = " ";
    public static final String ADD_BRICK_SERVER = "Server";
    public static final String ADD_BRICK_BRICK_DIRECTORY = "Brick Directory";
    public static final String ADD_BRICK_TABLE_REFERENCE = "AddBrickPopupView_bricksTable_col";

	/*
	 * User tab reference
	 */
	public static final String USER_FIRST_NAME = "First Name";
	public static final String USER_LAST_NAME = "Last Name";
	public static final String USER_NAME = "User Name";
	public static final String USER_Group = "Group";
	public static final String USER_EMAIL = "e-mail";
	public static final String USER_TABLE_REFERENCE = "MainTabUserView_table_content_col";
	
	public static final String HOOK_VOLUME_EVENT = "Volume Event";
	public static final String HOOK_STAGE = "Stage";
	public static final String HOOK_CONTENT_TYPE = "Content Type";
	
	/*
	 * 'Status' Values
	 */
    public enum Status{
    	UP("Up"),
    	DOWN("Down"),
    	MAINTENANCE("Maintenance");
		
		String value;
		Status(String value){
			this.value=value;
		}
		public String get(){
			return this.value;
		}
	}
	
	/*
	 * Select Table
	 */
	public enum TableType{
		CLUSTERS,SERVERS,VOLUMES,VOLUME_OPTIONS;
	}
	
	public static HashMap<String, String> getCluster(StorageBrowser storageTasks, String clusterName){
		LinkedList<HashMap<String, String>> table = getClustersTable(storageTasks);
		for(HashMap<String, String> row : table){
			if(row.get(GuiTables.NAME).equals(clusterName)){
				return row;
			}
		}
		return null;
	}	
	
	public static LinkedList<HashMap<String, String>> getClustersTable(StorageBrowser storageTasks){
		LinkedList<String> keys = new LinkedList<String>();
		keys.add(GuiTables.NAME);
		keys.add(GuiTables.DATA_CENTER);
		keys.add(GuiTables.COMPATIBLITY_VERSION);
		keys.add(GuiTables.DESCRIPTION);
		return getTable(storageTasks, GuiTables.CLUSTERS_TABLE_REFERENCE, keys);
	}
	
	public static HashMap<String, String> getServer(StorageBrowser storageTasks, String serverName){
		LinkedList<HashMap<String, String>> table = getServersTable(storageTasks);
		for(HashMap<String, String> row : table){
			if(row.get(GuiTables.NAME).equals(serverName)){
				return row;
			}
		}
		return null;
	}
	
	public static LinkedList<HashMap<String, String>> getServersTable(StorageBrowser storageTasks){
		LinkedList<String> keys = new LinkedList<String>();
		keys.add(GuiTables.NAME);
		keys.add(GuiTables.HOST_IP);
		keys.add(GuiTables.CLUSTER);
		keys.add(GuiTables.STATUS);
		return getTable(storageTasks, GuiTables.SERVERS_TABLE_REFERENCE, keys);
	}
	
	public static LinkedList<HashMap<String, String>> getAddBrickTable(StorageBrowser storageTasks){
		LinkedList<String> keys = new LinkedList<String>();
		keys.add(GuiTables.ADD_BRICK_SERVER);
		keys.add(GuiTables.ADD_BRICK_BRICK_DIRECTORY);
		return getTable(storageTasks, GuiTables.ADD_BRICK_TABLE_REFERENCE, keys);
	}
	
	public static HashMap<String, String> getVolume(StorageBrowser storageTasks, String volumeName){
		LinkedList<HashMap<String, String>> table = getVolumesTable(storageTasks);
		for(HashMap<String, String> row : table){
			if(row.get(GuiTables.NAME).equals(volumeName)){
				_logger.log(Level.INFO, "Row: "+row);
				return row;
			}
		}
		return null;
	}
	
	public static LinkedList<HashMap<String, String>> getVolumesTable(StorageBrowser storageTasks){
		ArrayList<HashMap<String, String>> volumesTable = new VolumeTable(storageTasks).getData();
		LinkedList<HashMap<String, String>> tmpVolumesTable = new LinkedList<HashMap<String, String>>();
		for(HashMap<String, String> row : volumesTable) {
			tmpVolumesTable.addLast(row);
		}
		return tmpVolumesTable;
		// This block is being commented because of the two columns introduced in the Bricks to show no.of bricks up and no.of bricks down.since the below code does not work commenting it.
		/*LinkedList<String> keys = new LinkedList<String>();
		keys.add(GuiTables.NAME);
		keys.add(GuiTables.CLUSTER);
		keys.add(GuiTables.VOLUME_TYPE);
		keys.add(GuiTables.NUMBER_OF_BRICKS);
		keys.add(GuiTables.ACTIVITIES);
		keys.add(GuiTables.STATUS);
		return getTable(storageTasks, GuiTables.VOLUME_TABLE_REFERENCE, keys);*/
	}

    public static HashMap<String, String> getVolumeOption(StorageBrowser storageTasks, String optionName, ElementStub nearReference){
		LinkedList<HashMap<String, String>> table = getVolumesOptionsTable(storageTasks, nearReference);
		for(HashMap<String, String> row : table){
			if (row.get(GuiTables.VOLUME_OPTION_KEY).equals(optionName)){
				_logger.log(Level.INFO, "Row: "+row);
				return row;
			}
		}
		return null;
    }
    /*
     * returns volume options table
     */
	public static LinkedList<HashMap<String, String>> getVolumesOptionsTable(StorageBrowser storageTasks, ElementStub nearReference){
		LinkedList<String> keys = new LinkedList<String>();
		keys.add(GuiTables.VOLUME_OPTION_KEY);
		keys.add(GuiTables.VOLUME_OPTION_VALUE);
		//ElementStub nearRef = storageTasks.div("Reset All");
		return getTableCore(storageTasks, GuiTables.VOLUME_OPTION_TABLE_REFERENCE, nearReference, keys);
	}
	
    /*
     * returns cluster.services table
     */
    public static LinkedList<HashMap<String, String>> getClusterServicesTable(StorageBrowser storageTasks, ElementStub nearReference){
    	LinkedList<String> keys = new LinkedList<String>();
        keys.add(GuiTables.CLUSTER_SERVICES_HOST);
        keys.add(GuiTables.CLUSTER_SERVICES_SERVICE);
        keys.add(GuiTables.CLUSTER_SERVICES_STATUS);
        keys.add(GuiTables.CLUSTER_SERVICES_PORT);
        keys.add(GuiTables.CLUSTER_SERVICES_PROCESS_ID);
        return getTableCore(storageTasks, GuiTables.CLUSTER_SERVICES_TABLE_REFERENCE, nearReference, keys);
    }

	/*
	 * Returns brick table
	 */
	public static LinkedList<HashMap<String, String>> getBricksTable(StorageBrowser storageTasks, ElementStub nearReference){
		LinkedList<String> keys = new LinkedList<String>();
		keys.add(GuiTables.BRICK_SERVER);
		keys.add(GuiTables.BRICK_DIRECTORY);
		keys.add(GuiTables.ACTIVITIES);
		nearReference = storageTasks.div(6).near(storageTasks.div("Add"));
		return getTableCore(storageTasks, GuiTables.BRICK_TABLE_REFERENCE, nearReference, keys);
	}
	
	/*
	 * Returns events table in basic view
	 */
	public static LinkedList<HashMap<String, String>> getEventsTableBasicView(StorageBrowser storageTasks, ElementStub nearReference){
		LinkedList<String> keys = new LinkedList<String>();
		keys.add(GuiTables.EVENT_TIME);
		keys.add(GuiTables.EVENT_MESSAGE);
		return getTableCore(storageTasks, GuiTables.BRICK_TABLE_REFERENCE, nearReference, keys);
	}

	/*
	 * Returns events table in advanced view
	 */
	public static LinkedList<HashMap<String, String>> getEventsTableAdvancedView(StorageBrowser storageTasks, ElementStub nearReference){
		LinkedList<String> keys = new LinkedList<String>();
		keys.add(GuiTables.EVENT_TIME);
		keys.add(GuiTables.EVENT_MESSAGE);
		keys.add(GuiTables.EVENT_ID);
		keys.add(GuiTables.EVENT_USER);
		keys.add(GuiTables.EVENT_HOST);
		keys.add(GuiTables.EVENT_CLUSTER);
		keys.add(GuiTables.EVENT_VOLUME);
		keys.add(GuiTables.EVENT_CORRELATION_ID);
		return getTableCore(storageTasks, GuiTables.BRICK_TABLE_REFERENCE, nearReference, keys);
	}

	public static LinkedList<HashMap<String, String>> getUsersTable(StorageBrowser storageTasks){
		LinkedList<String> keys = new LinkedList<String>();
		keys.add(GuiTables.USER_FIRST_NAME);
		keys.add(GuiTables.USER_LAST_NAME);
		keys.add(GuiTables.USER_NAME);
		keys.add(GuiTables.USER_Group);
		keys.add(GuiTables.USER_EMAIL);
		return getTable(storageTasks, GuiTables.USER_TABLE_REFERENCE, keys);
	}
	
	/*
	 * get Table without "near" reference
	 */
	public static LinkedList<HashMap<String, String>> getTable(StorageBrowser storageTasks, String cellReference, LinkedList<String> keys){
		return getTableCore(storageTasks, cellReference, null, keys);	
	}
	
	/*
	 * base table method
	 */
	public static LinkedList<HashMap<String, String>> getTableCore(StorageBrowser storageTasks, String cellReference, ElementStub nearReference, LinkedList<String> keys){
		LinkedList<HashMap<String, String>> table = new LinkedList<HashMap<String,String>>();
		HashMap<String,String> row = new HashMap<String,String>();
		int cellCount;
		if(nearReference == null){
			cellCount = storageTasks.div("/"+cellReference+"/").countSimilar();
		}else{
			cellCount = storageTasks.div("/"+cellReference+"/").near(nearReference).countSimilar();
		}
		//_logger.log(Level.FINE, "REF: "+cellReference+" and nearRef:" + nearReference+", Cell Count and keys.size(): "+cellCount + " " + keys.size());
		if(cellCount%keys.size() != 0){
			//TODO: throw exception
			_logger.log(Level.WARNING, "Column count missmatch with actual column!!");
		}
		int cellNo=0;
		String cellValue = null;
		while(cellNo < cellCount){
			for(String key : keys){
				if (!key.equals(GuiTables.ACTIVITIES)) {
					if(nearReference == null){
						cellValue = storageTasks.div("/"+cellReference+"/["+cellNo+"]").getText().trim();
					}else{
						cellValue = storageTasks.div("/"+cellReference+"/["+cellNo+"]").near(nearReference).getText().trim();
					}
					//_logger.log(Level.INFO, "cellCount: " + cellCount + "  cellNo: " + cellNo + "  key: " + key + "  cellValue: " + cellValue);
					row.put(key, cellValue);
					cellNo++;
				}
			}
			table.add(row);
			row = new HashMap<String,String>();
		}
		return table;		
	}
	
	/*
	 * Wait and retry
	 */
	public static HashMap<String, String> waitAndGetExpectedResult(StorageBrowser storageTasks, TableType type, String rowReference, String referenceKey, String expectedValue, long waitTime, int retryCount){
		return waitAndGetExpectedResultCore(storageTasks, type, rowReference, referenceKey, null, expectedValue, waitTime, retryCount);
	}
	/*
//	 * Wait and retry - Core
	 */
	public static HashMap<String, String> waitAndGetExpectedResultCore(StorageBrowser storageTasks, TableType type, String rowReference, String referenceKey, ElementStub nearReference, String expectedValue, long waitTime, int retryCount){
		HashMap<String, String> row = null;
        String tabName = null;
        if(type.name().equals("CLUSTERS")){
        	tabName = "Cluster";
		} else if(type.name().equals("SERVERS")){
			tabName = "Host";
		} else if(type.name().equals("VOLUMES")||type.name().equals("VOLUME_OPTIONS")){
			tabName = "Volume";
		}
		for(int i=0; i<retryCount;i++){
			_logger.log(Level.FINE, "Attempt: ["+(i+1)+" of "+retryCount+"]");
			storageTasks.clickRefresh(tabName);
			switch(type){
			case CLUSTERS:
				row = GuiTables.getCluster(storageTasks, rowReference);
				break;
			case SERVERS:
				row = GuiTables.getServer(storageTasks, rowReference);
				break;
			case VOLUMES:
				row = GuiTables.getVolume(storageTasks, rowReference);
				break;
            case VOLUME_OPTIONS:
                row = GuiTables.getVolumeOption(storageTasks, rowReference, nearReference);
                break;
			}
			if((row != null) && row.get(referenceKey).equals(expectedValue)){
				_logger.log(Level.FINE, "Success: Expected result["+expectedValue+"] for the reference Key["+referenceKey+"] from the row["+rowReference+"]!");
				return row;
			}else {
				storageTasks.waitFor(waitTime);
			}
		}
		_logger.log(Level.WARNING, "Failed to get the expected result["+expectedValue+"] for the reference Key["+referenceKey+"] from the row["+rowReference+"]!");
		return row;
	}
	
	public static int getRowNumber(StorageBrowser storageTasks, String cellReference, String cellValueToFind) {
		int rowNumber = -1;
		String cellValue = null;
		
		int cellCount = storageTasks.div("/"+ cellReference + "/").countSimilar();
		
		for (int x = 0; x < cellCount; x++) {
			cellValue = storageTasks.div("/" + cellReference + "/[" + x + "]").getText().trim();
			if (cellValue.equals(cellValueToFind)) {
				rowNumber = x;
				break;
			}
		}	
		return rowNumber;
	}

	public static ElementStub getCell(StorageBrowser storageTasks, String reference, int colIndex, int rowIndex){
		return storageTasks.div("/" + reference + colIndex +"_row" +rowIndex + "$/");
	}
	
	public static ElementStub getFirstCellByRow(StorageBrowser storageTasks, String reference, int rowIndex){
		return storageTasks.div("/" + reference  +"\\d_row" +rowIndex + "$/");
	}
}
