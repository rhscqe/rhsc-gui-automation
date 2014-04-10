/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;



import org.calgb.test.performance.HttpSession;

import com.redhat.qe.factories.ClusterFactory;
import com.redhat.qe.model.Cluster;
import com.redhat.qe.model.Host;
import com.redhat.qe.model.WaitUtil;
import com.redhat.qe.ovirt.shell.RhscShellSession;
import com.redhat.qe.repository.IClusterRepository;
import com.redhat.qe.repository.IHostRepository;
import com.redhat.qe.repository.rest.HostRepository;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.te.Server;

/**
 * @author dustin 
 * Aug 26, 2013
 */
public class ServerFixtureHelper{
	
//	public void createServer(RhscShellSession session, ServerMap server){
//		Cluster cluster = getCluster(server);
//		cluster = new ClusterRepository(session).createOrShow(cluster);
//		createAndWaitForUp(session, server.toHost());
//	}

	
	public Host createServer(RepositoryContainer repositories, Server server,Cluster cluster){
		cluster = repositories.getClusterRepository().createOrShow(cluster);
		Host host = server.toHost();
		host.setCluster(cluster);
		return createServer(repositories, host);
	}
	public Host createServer(RepositoryContainer repositories, Host host){
		repositories.getClusterRepository().createOrShow(host.getCluster());
		return createAndWaitForUp(repositories.getHostRepository(), host);
	}

	public Host createServer(RepositoryContainer repositories, ServerMap serverMap){
		return createServer( repositories,  serverMap.toHost());
	}
	

	private Host createAndWaitForUp(IHostRepository hostRepository, Host host) {
		host = hostRepository.createOrShow(host);
		if(host.getState().equals("maintenance"))
			hostRepository.activate(host);
		WaitUtil.waitForHostStatus(hostRepository, host, "up", 30);
		return host;
	}

	/**
	 * @param server
	 */
	private Cluster getCluster(ServerMap server) {
		Cluster cluster = ClusterFactory.cluster(server.getClusterName(), server.getClusterDescription());
		return cluster;
	}

}
