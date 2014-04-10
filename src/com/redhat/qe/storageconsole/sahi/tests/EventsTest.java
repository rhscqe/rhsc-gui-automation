package com.redhat.qe.storageconsole.sahi.tests;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.bind.JAXBException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiEventTasks;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

public class EventsTest extends SahiTestBase {

	StorageSahiEventTasks tasks = null;
	
	@BeforeMethod
	public void setUp() {
		tasks = new StorageSahiEventTasks(browser);
	}

	// Validate Basic View
	@Test
	public void eventsBasicView() {
		Assert.assertTrue(tasks.validateEventsBasicView(), "Basic View Not Correct!");
	}
	
	// Validate Advanced View
	@Test
	public void eventsAdvancedView() {
		Assert.assertTrue(tasks.validateEventsAdvancedView(), "Advanced View Not Correct!");
	}
	
	// Generate an Event - Validate event is on list
	@Test
	public void generateAndThenValidateEvent() throws IOException, JAXBException, TestEnvironmentConfigException {
		Assert.assertTrue(tasks.generateAndThenValidateEvent(), "Failed to generate Event!");
	}
	
	// Validate Next and Previous page buttons
	@Test
	public void validatePagingButtons() throws ParseException {
		Assert.assertTrue(tasks.validatePagingButtons(), "Page buttons!");
	}
}
