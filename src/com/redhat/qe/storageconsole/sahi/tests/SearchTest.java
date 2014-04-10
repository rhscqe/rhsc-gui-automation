package com.redhat.qe.storageconsole.sahi.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiSearchTasks;

public class SearchTest extends SahiTestBase {
	StorageSahiSearchTasks tasks = null;
	
	@BeforeMethod
	public void setUp() {
		tasks = new StorageSahiSearchTasks(browser);
	}
	
	@Test
	public void searchByClusterName() {
		Assert.assertTrue(tasks.searchByClusterName(), "Search by Cluster Name!");
	}
	
	@Test
	public void searchByClusterDescription() {
		Assert.assertTrue(tasks.searchByClusterDescription(), "Search by Cluster Description!");
	}
	
	@Test
	public void searchByHostName() {
		Assert.assertTrue(tasks.searchByHostName(), "Search by Host Name!");
	}
	
	@Test
	public void searchByHostStatus() {
		Assert.assertTrue(tasks.searchByHostStatus(), "Search by Host Status!");
	}
	
	@Test
	public void searchByVolumeName() {
		Assert.assertTrue(tasks.searchByVolumeName(), "Search by Volume Name!");
	}
	
	@Test(enabled=true)
	public void searchByVolumeType() {
		Assert.assertTrue(tasks.searchByVolumeType(), "Search by Volume Status!");
	}
	
	@Test
	public void searchByUserName() {
		Assert.assertTrue(tasks.searchByUserName(), "Search by User Name!");
	}
}
