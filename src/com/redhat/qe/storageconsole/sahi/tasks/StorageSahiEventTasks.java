/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import net.sf.sahi.client.ElementStub;
import com.redhat.qe.storageconsole.mappper.ClusterMap;
import com.redhat.qe.storageconsole.te.ClusterCompatibilityVersion;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author mmahoney 
 * Nov 14, 2012
 */
public class StorageSahiEventTasks {

	ElementStub NEAR_REF_EVENTS_TABLE = null;
	StorageBrowser storageSahiTasks = null;
	StorageSahiClusterTasks clusterTasks = null;
	StorageSahiEventMessageTasks storageSahiEventMessageTasks = null;
	
	final String cellReference = "gwt-uid-.*_col";
	int maxColumns = 9; // Advanced View Column count + 1
	int wait = 1500;
	int retryCount = 30;
	
	public StorageSahiEventTasks(StorageBrowser tasks) {
		storageSahiTasks = tasks;
		NEAR_REF_EVENTS_TABLE = storageSahiTasks.label("Advanced View");
		clusterTasks = new StorageSahiClusterTasks(storageSahiTasks);
		storageSahiEventMessageTasks = new StorageSahiEventMessageTasks(storageSahiTasks);
	}
	
	// Validate that Event Basic View only displays "basic" columns.
	public boolean validateEventsBasicView() {
		
		if(!storageSahiTasks.selectPage("Events")) {
			return false;
		}
		
		storageSahiTasks.radio(0). near(storageSahiTasks.label("Basic View")).click();  // Basic View Radio Button view

		storageSahiEventMessageTasks.waitForTableLoad();
		
		// Validate that the Advance View Column is not present
		if (storageSahiTasks.div(2).containsText("Event ID")) {
			storageSahiTasks._logger.log(Level.WARNING, "Unexpected number of columns in Events Basic View!");
			return false;
		}
		
		return true;
	}

	// Validate that Event Advanced View only displays all columns.
	public boolean validateEventsAdvancedView() {
		
		if(!storageSahiTasks.selectPage("Events")) {
			return false;
		}
		
		storageSahiTasks.radio(0).near(storageSahiTasks.label("Advanced View")).click();  // Advanced View Radio Button 
		
		storageSahiEventMessageTasks.waitForTableLoad();
		
		// Validate that the Advance View Column is present
		if (!storageSahiTasks.div(2).containsText("Event ID")) {
			storageSahiTasks._logger.log(Level.WARNING, "Unexpected number of columns in Events Basic View!");
			return false;
		}
		
		return true;
	}
	
	// Generate and Event, and the validate that the event is listed in the Event table.
	public boolean generateAndThenValidateEvent() throws IOException, JAXBException, TestEnvironmentConfigException {
		final String clusterName = "automation_generateEvent_" + System.currentTimeMillis(); // Add some uniqueness to the Cluster Name
		final String expectedMessage = "Cluster " + clusterName + " was added by";
		
		// Generate Event - Create A New Cluster
		
		ClusterMap clusterMap = new ClusterMap();
		clusterMap.setResourceLocation("System->System->Clusters");
		clusterMap.setClusterName(clusterName);
		clusterMap.setClusterDescription("Created by automation code");
		ClusterCompatibilityVersion clusterCompatibilityVersion = TestEnvironmentConfig.getTestEnvironemt().getClusterCompatibilityVersion();
		clusterMap.setClusterCompatibilityVersion(clusterCompatibilityVersion.toString());
		boolean clusterResult = clusterTasks.createNewCluster(clusterMap);
		
		if(!clusterResult) {
			storageSahiTasks._logger.log(Level.WARNING, "Unable to create Cluster event!");
			return false;
		}
		
		if(!storageSahiTasks.selectPage("Events")) {
			cleanupClusterEventData(clusterMap);
			return false;
		}
		
		storageSahiEventMessageTasks.waitForTableLoad();
		
		// Validate that the Event is in table by checking that row-0 Message contains Cluster name.
		
		if (!storageSahiTasks.div(2).containsText(expectedMessage)) {
			storageSahiTasks._logger.log(Level.WARNING, "Event message [" + expectedMessage + "] not found!");
			cleanupClusterEventData(clusterMap);
			return false;
		}
		
		// Clean up - Remove Cluster.
		
		cleanupClusterEventData(clusterMap);
		
		return true;
		
	}
	
	// Validate the ">" (Next) and "<" (Previous) page buttons.
	// Expectation: Events are listed in descending order of date/time.
	public boolean validatePagingButtons() throws ParseException {
		//final String timeReference = "/" + cellReference + "1_row0" + "/";
		//ElementStub timeReference = storageSahiTasks.div("/"+cellReference+"/"+"[0]").near(storageSahiTasks.tableHeader("Time")).near(storageSahiTasks.tableHeader("Message"));
		ElementStub timeReference = storageSahiTasks.div("/" + GuiTables.EVENT_TABLE_REFERENCE + "/" +  "[0]").near(NEAR_REF_EVENTS_TABLE);
		String dateTimeStamp = null;
		long timeStampBeforePaging = -1;
		long timeStampAfterPaging = -1;
		
		if(!storageSahiTasks.selectPage("Events")) {
			return false;
		}
		
		storageSahiEventMessageTasks.waitForTableLoad();
		
		// Validate ">" (Next page) button
		if (!storageSahiTasks.image("clear.cache.gif[0]").near(storageSahiTasks.label("Basic View")).isVisible()) {
			storageSahiTasks._logger.log(Level.INFO, "No events to page \"Next\"!");
			return true;
		}
		
		// Get date/time stamp of the first Event before clicking ">" button
		//dateTimeStamp = storageSahiTasks.div(timeReference).getText().trim();
		dateTimeStamp = timeReference.getText().trim();
		timeStampBeforePaging = getTimeInMilliseconds(dateTimeStamp);
		
		// click Next button
		storageSahiTasks.image("clear.cache.gif[0]").near(storageSahiTasks.label("Basic View")).click();
		
		storageSahiEventMessageTasks.waitForTableLoad();		
		// Get data/time stamp after paging, and then validate that it is an
		// earlier time than the first date/time stamp.
		dateTimeStamp = timeReference.getText().trim();
		timeStampAfterPaging = getTimeInMilliseconds(dateTimeStamp);
		if (timeStampBeforePaging <= timeStampAfterPaging) {
			storageSahiTasks._logger.log(Level.WARNING, "Next page failed!");
			return false;
		}
		
		// Validate "<" (Previous page) button
		
		timeStampBeforePaging = timeStampAfterPaging;
		
		if (!storageSahiTasks.image("clear.cache.gif[1]").near(storageSahiTasks.label("Basic View")).isVisible()) {
			storageSahiTasks._logger.log(Level.WARNING, "\"Previous\" button not enabled!");
			return false;
		}
		
		// click Previous button
		storageSahiTasks.image("clear.cache.gif[1]").near(storageSahiTasks.label("Basic View")).click();
		storageSahiEventMessageTasks.waitForTableLoad();

		// Get data/time stamp after paging, and then validate that it is a
		// later time than the first datevalidatePagingButtons/time stamp.
		dateTimeStamp = timeReference.getText().trim();
		timeStampAfterPaging = getTimeInMilliseconds(dateTimeStamp);
		if (timeStampAfterPaging <= timeStampBeforePaging) {
			storageSahiTasks._logger.log(Level.WARNING, "Previous page failed!");
			return false;
		}
		
		return true;
	}
	
	private boolean cleanupClusterEventData(ClusterMap clusterMap) {
		
		boolean clusterResult = clusterTasks.removeCluster(clusterMap);
		if(!clusterResult) {
			storageSahiTasks._logger.log(Level.WARNING, "Unable to remove Cluster!");
			return false;
		}
		
		return true;
	}
	
	private long getTimeInMilliseconds(String dateAndTime) throws ParseException {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");
		return new SimpleDateFormat("yyyy-MMM-dd, HH:mm").parse(dateAndTime).getTime();
	}
}
