/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.redhat.qe.model.WaitUtil;
import com.redhat.qe.model.WaitUtil.WaitResult;
import com.redhat.qe.storageconsole.helpers.RegexMatch;
import com.redhat.qe.storageconsole.helpers.elements.EventTabTable;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JsGeneric;
import com.redhat.qe.storageconsole.helpers.pages.components.MainTabPanel;

import dstywho.functional.Predicate;

/**		WaitResult isEventTableContainEvent= WaitUtil.waitUntil(new Predicate() {
			
			@Override
			public Boolean act() {
				return eventTabTable.getJqueryObject().find("tr:lt(10)").addCall("text").fetch(storageSahiTasks).contains(logMsg);
			}
		}, retryCount);
		
 * @author mmahoney 
 * Apr 30, 2013
 */
public class StorageSahiEventMessageTasks {
	
	/**
	 * 
	 */
	private static final int FIFTY = 50;
	private static final Logger LOG = Logger.getLogger(StorageSahiEventMessageTasks.class.getName());
	ElementStub NEAR_REF_EVENTS_TABLE = null;
	StorageBrowser storageSahiTasks = null;
	
	int wait = 1500;
	int retryCount = 20;
	
	public StorageSahiEventMessageTasks(StorageBrowser tasks) {
		storageSahiTasks = tasks;
		NEAR_REF_EVENTS_TABLE = storageSahiTasks.label("Advanced View");
	}
	
	public boolean validateLogMessage(final String logMsg) {
		LOG.log(Level.INFO, "starting to validate event message");
		int retryCount = 20;
		
		storageSahiTasks.selectPage("Events"); 		

		waitForTableLoad();
		
		storageSahiTasks.radio(0).near(storageSahiTasks.label("Advanced View")).click();  // Advanced View Radio Button 

		final EventTabTable eventTabTable = getEventTable();
		WaitResult isEventTableContainEvent = waitUntilEventTableContainsExpectedMessage(logMsg, retryCount, eventTabTable);
		
		Assert.assertTrue(isEventTableContainEvent.isSuccessful(), "Failed to find Event Message: " + logMsg + ".\n" + "the last 5 messages are:" +  formattedMostRecentMessages(eventTabTable, FIFTY));
		return true;
		
	}

	/**
	 * @return
	 */
	private EventTabTable getEventTable() {
		return new EventTabTable(storageSahiTasks);
	}

	/**
	 * @param logMsg
	 * @param retryCount
	 * @param eventTabTable
	 * @return
	 */
	private WaitResult waitUntilEventTableContainsExpectedMessage( final String logMsg, int retryCount, final EventTabTable eventTabTable) {
		WaitResult isEventTableContainEvent= WaitUtil.waitUntil(new Predicate() {
			
			@Override
			public Boolean act() {
				new MainTabPanel(storageSahiTasks).clickRefresh();
				String textFromFirstTenRows = mostRecentEventMessages(eventTabTable, 50).addCall("text").fetch(storageSahiTasks);
				LOG.log(Level.FINEST, textFromFirstTenRows);
				return ! new RegexMatch(textFromFirstTenRows).find(logMsg).isEmpty();
			}

		}, retryCount);
		return isEventTableContainEvent;
	}

	private JQuery mostRecentEventMessages( EventTabTable eventTabTable, int numMessages) {
		return eventTabTable.getJqueryObject().find(String.format("tr:lt(%s)", numMessages));
	}
	
	private ArrayList<String> getMessagesList(JQuery messages){
		Type messageList= new TypeToken<ArrayList<String>>(){}.getType();
		String json = messages.addCall("map", new JsGeneric("function(){ return jQuery(this).text(); }")).addCall("get").fetchToJson(storageSahiTasks);
		return new Gson().fromJson(json, messageList);
	}
	
	private String formattedMostRecentMessages(EventTabTable eventTabTable, int numMessages){
		return Joiner.on(",\n").join( getMessagesList(mostRecentEventMessages(eventTabTable, numMessages))).toString();
	}
	
	/**
	 * 
	 */
	public void waitForTableLoad() {
		LOG.log(Level.INFO, "waiting for event table to load");
		Assert.assertTrue(getEventTable().waitUntilVisible(), "event table is visible");
		LOG.log(Level.INFO, "event table loaded");
		
	}
}
