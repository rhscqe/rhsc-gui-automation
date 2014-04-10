/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.tags;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.elements.Dialog;
import com.redhat.qe.storageconsole.helpers.elements.TagModal;
import com.redhat.qe.storageconsole.helpers.pages.components.LHSAccordion;
import com.redhat.qe.storageconsole.listeners.depend.DependsOn;
import com.redhat.qe.storageconsole.mappper.ClusterMap;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.mappper.Tag;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiClusterTasks;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiServerTasks;
import com.redhat.qe.storageconsole.sahi.tasks.TaggingTasks;
import com.redhat.qe.storageconsole.sahi.tasks.UsersMainTabTasks;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironment;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 * @author dustin Jul 16, 2013
 * 
 */
public class TagUserTest extends SahiTestBase {

	/**
	 * 
	 */
	private static final Tag ROOT = new Tag("Root");
	private static Tag TAG = new Tag("usertag", "this is my auto tag");
	private static Tag TAG_TO_REMOVE;
	private ServerMap server;
	private ClusterMap cluster;

	@BeforeMethod
	public void beforeMyMethods() {
		new TaggingTasks(getBrowser()).createIfNotPresent(TAG, ROOT);
	}

	/**
	 * @return
	 */
	private UsersMainTabTasks getUserTasks() {
		return new UsersMainTabTasks(getBrowser());
	}

	@AfterMethod
	public void removeTag() {
		getTaggingTask().removeTag(TAG);
		if (TAG_TO_REMOVE != null)
			getTaggingTask().removeTag(TAG_TO_REMOVE);
	}

	private TaggingTasks getTaggingTask() {
		return new TaggingTasks(browser);
	}


	@Test
	@Tcms("244665")
	public void tagUser() {
		getUserTasks().navigateToTab();
		String userFirstName = "admin";
		getUserTasks().selectUser(userFirstName);			
		getUserTasks().tagUser(userFirstName, TAG);
	}



}
