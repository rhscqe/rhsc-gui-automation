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
import com.redhat.qe.storageconsole.helpers.pages.components.SearchPanel;
import com.redhat.qe.storageconsole.listeners.depend.DependsOn;
import com.redhat.qe.storageconsole.mappper.ClusterMap;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.mappper.Tag;
import com.redhat.qe.storageconsole.mappper.TagFactory;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiClusterTasks;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiServerTasks;
import com.redhat.qe.storageconsole.sahi.tasks.TaggingTasks;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironment;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 * @author dustin Jul 16, 2013
 * 
 */
public class TagServerTest extends SahiTestBase {

	/**
	 * 
	 */
	private static final Tag ROOT = new Tag("Root");
	private static Tag TAG = TagFactory.create("TagServerTest; tag test");
	private static Tag TAG2 = TagFactory.create("TagServerTest: second tag");

	private static Tag TAG_TO_REMOVE;
	private ServerMap server;
	private ClusterMap cluster;
	private ServerMap server2;

	@BeforeMethod
	public void beforeMyMethods() {
		getTaggingTask().createIfNotPresent(TAG, ROOT);
		createServerAndClusters();
		browser.selectPage(server.getResourceLocation());
	}

	/**
	 * 
	 */
	private void createServerAndClusters() {
		cluster =ClusterMap.clusterMap("taggingSuiteCluster");
		new StorageSahiClusterTasks(browser).createNewCluster(cluster);
		
		server = ServerMap.fromServer(TestEnvironmentConfig.getTestEnvironment().getServers().get(0), cluster.getClusterName());
		server2 = ServerMap.fromServer(TestEnvironmentConfig.getTestEnvironment().getServers().get(1), cluster.getClusterName());
		new StorageSahiServerTasks(browser).addServer(server);
		new StorageSahiServerTasks(browser).addServer(server2);
	}

	@AfterMethod
	public void removeTag() {
		getTaggingTask().removeTag(TAG);
		if (TAG_TO_REMOVE != null)
			getTaggingTask().removeTag(TAG_TO_REMOVE);
		removeServerAndClusters();
	}

	/**
	 * 
	 */
	private void removeServerAndClusters() {
		new StorageSahiServerTasks(browser).removeServer(server);
		new StorageSahiServerTasks(browser).removeServer(server2);
		new StorageSahiClusterTasks(browser).removeCluster(cluster);
	}

	private TaggingTasks getTaggingTask() {
		return new TaggingTasks(browser);
	}

	@Test
	@Tcms({"244663","260596"})
	public void tagServer() {
		new StorageSahiServerTasks(browser).tagServer(server, TAG);
		new StorageSahiServerTasks(browser).untagServer(server, TAG);
	}
	
	@Test
	@Tcms("260598")
	public void searchForRenamedTag(){
		new StorageSahiServerTasks(browser).tagServer(server, TAG);
		getTaggingTask().edit(TAG,TAG2);
		TAG_TO_REMOVE = TAG2;
		browser.selectPage("Hosts");
		
		
		new SearchPanel(browser).search(String.format("Host: tag = %s", TAG2.getName()));
		
		Assert.assertTrue(browser.div(server.getServerName()).isVisible());
		Assert.assertFalse(browser.div(server2.getServerName()).isVisible());

		new SearchPanel(browser).search("Host:");

	}


}
