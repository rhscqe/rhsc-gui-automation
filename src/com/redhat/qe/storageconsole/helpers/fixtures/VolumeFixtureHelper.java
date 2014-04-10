/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;

import com.google.common.base.Predicate;
import com.redhat.qe.model.Brick;
import com.redhat.qe.model.Cluster;
import com.redhat.qe.model.Host;
import com.redhat.qe.model.Volume;
import com.redhat.qe.storageconsole.helpers.CollectionsExtras;
import com.redhat.qe.storageconsole.helpers.cli.BrickHelper;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Aug 26, 2013
 */
public class VolumeFixtureHelper{
	
	public VolumeArtifactsHolder create(RepositoryContainer repos, VolumeMap volumeMap) throws TestEnvironmentConfigException {
		new BrickHelper().deleteDirForBricks(volumeMap);
		new BrickHelper().createDirForBricks(volumeMap);
		ArrayList<Host> hosts = createServers(repos, volumeMap);
		
		Volume vol = setHostIdForBricks(volumeMap.toVolume(), repos);
		Cluster cluster = hosts.get(0).getCluster();
		vol.setCluster(cluster);

		Volume volume = repos.getVolumeRepositoryFactory().getVolumeRepository(cluster).create(vol);
		return new VolumeArtifactsHolder(volume, volumeMap, hosts, cluster);
	}


	/**
	 * @param session
	 * @param volumeMap
	 * @return 
	 * @throws TestEnvironmentConfigException
	 */
	private ArrayList<Host> createServers(RepositoryContainer repos, VolumeMap volumeMap) throws TestEnvironmentConfigException {
		ArrayList<Host> result = new ArrayList<Host>();
		
		Cluster cluster = volumeMap.toVolume().getCluster();
		for(Server server : volumeMap.getConfiguredServers()){
			result.add( new ServerFixtureHelper().createServer(repos, server, cluster));
		}
		return result;
	}
	
	
	
	
	private Volume setHostIdForBricks(Volume volume, RepositoryContainer repos){

		ArrayList<Brick> bricks = new ArrayList<Brick>();
		List<Host> hosts = repos.getHostRepository().list();
		
		for(final Brick brick: volume.getBricks()){
			Host host = CollectionsExtras.findFirst(hosts, new Predicate<Host>() {

				@Override
				public boolean apply(Host host) {
					return brick.getHost().getName().equals(host.getName());
				}
			});
			Assert.assertNotNull(host, "host not found for brick");
			brick.setHost(host);
			bricks.add(brick);
		}
		volume.setBricks(bricks);
		return volume;

	}
	


}
