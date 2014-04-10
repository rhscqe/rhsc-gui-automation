/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.testng.Assert;

import com.redhat.qe.factories.ClusterFactory;
import com.redhat.qe.factories.VolumeFactory;
import com.redhat.qe.helpers.repository.HostRepositoryHelper;
import com.redhat.qe.helpers.repository.VolumeRepositoryHelper;
import com.redhat.qe.model.Cluster;
import com.redhat.qe.model.Host;
import com.redhat.qe.model.Volume;
import com.redhat.qe.model.WaitUtil;
import com.redhat.qe.repository.rest.ClusterRepository;
import com.redhat.qe.repository.rest.HostRepository;
import com.redhat.qe.repository.rest.VolumeRepository;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.te.ClusterCompatibilityVersion;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin Mar 11, 2014
 */
public class VolumeTestBase extends RestFixtureTestBase {

	private static final String CLUSTER_NAME = "myCluster";
	private static final String VOLUME_NAME = "myVolume";
	private Cluster cluster;
	private Host host1;
	private Host host2;
	private Volume volume;

	@Override
	public void setupData(RepositoryContainer repos)
			throws TestEnvironmentConfigException {
		createVolume(repos);
	}

	
	public Server[] defineServers(){
		Server server1 = TestEnvironmentConfig.getTestEnvironment()
				.getServers().get(0);
		Server server2 = TestEnvironmentConfig.getTestEnvironment()
				.getServers().get(1);
		
		return new Server[]{server1, server2};
	}
	/**
	 * @param repos
	 */
	private void createVolume(RepositoryContainer repos) {
		cluster = new ClusterRepository(getSession())
				.createOrShow(defineCluster());


		host1 = new ServerFixtureHelper().createServer(repos, defineServers()[0], cluster);
		host2 = new ServerFixtureHelper().createServer(repos, defineServers()[1], cluster);

		volume = new VolumeRepository(getSession(), cluster)
				.createOrShow(volumeCreationStrategy());
	}

	/**
	 * @return
	 */
	private HostRepository getHostRepository() {
		return new HostRepository(getSession());
	}

	/**
	 * @return
	 */
	protected Volume volumeCreationStrategy() {
		return new GuiVolumeFactory().distributed(VOLUME_NAME, host1, host2);
	}

	/**
	 * @return
	 * 
	 */
	private Cluster defineCluster() {
		cluster = ClusterFactory.cluster("myCluster", "volume test");
		ClusterCompatibilityVersion version = TestEnvironmentConfig.getTestEnvironment()
				.getClusterCompatibilityVersion();
		cluster.setMajorVersion(Integer.parseInt(version.getMajor()));
		cluster.setMinorVersion(Integer.parseInt(version.getMinor()));
		return cluster;

	}
	

	public Cluster getCreateCluster() {
		return cluster;
	}

	public List<Host> getCreatedHosts() {
		return Arrays.asList(new Host[] { host1, host2 });
	}

	public Volume getCreatedVolume() {
		return volume;
	}

}
