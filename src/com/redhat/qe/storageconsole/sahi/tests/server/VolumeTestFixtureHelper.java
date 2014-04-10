/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.calgb.test.performance.HttpSession;

import com.redhat.qe.model.Host;
import com.redhat.qe.repository.rest.VolumeRepository;
import com.redhat.qe.storageconsole.helpers.fixtures.RepositoryContainer;
import com.redhat.qe.storageconsole.helpers.fixtures.RestApiConfiguration;
import com.redhat.qe.storageconsole.helpers.fixtures.ServerFixtureHelper;
import com.redhat.qe.storageconsole.helpers.fixtures.VolumeFixtureHelper;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.sahi.tests.ServerTest;
import com.redhat.qe.storageconsole.sahi.tests.VolumeTest;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Oct 7, 2013
 */
public class VolumeTestFixtureHelper {
	public static ArrayList<Host> setupFixtures(RepositoryContainer repos) throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		ArrayList<Host> hosts = new ArrayList<Host>();		
		Object[][] servers = new ServerTest().getServerCreationgData();
		for( int i = 0; i < servers.length; i ++){
			ServerMap serverMap = (ServerMap) servers[i][0];
			hosts.add(new ServerFixtureHelper().createServer(repos, serverMap));
		};
		return hosts;

	}
	
	public static void createVolumes(RepositoryContainer repos) throws TestEnvironmentConfigException, FileNotFoundException, IOException, JAXBException{
		
		Object[][] volumes = new VolumeTest().getVolumeCreationData();
		for( int i = 0; i < volumes.length; i ++){
			VolumeMap vmap = (VolumeMap) volumes[i][0];
			new VolumeFixtureHelper().create(repos, vmap);
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, JAXBException, TestEnvironmentConfigException{
		HttpSession session = new com.redhat.qe.helpers.rest.HttpSessionFactory().createHttpSession(RestApiConfiguration.getRestApi());
		RepositoryContainer repos = RepositoryContainer.getRepositoryContainer(session);
		setupFixtures(repos);
		createVolumes(repos);
	}

}
