/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import org.testng.annotations.BeforeMethod;

import com.redhat.qe.factories.ClusterFactory;
import com.redhat.qe.model.Cluster;
import com.redhat.qe.repository.rest.ClusterRepository;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Mar 19, 2014
 */
public class ClusterTestBase extends RestFixtureTestBase{

	
	public Cluster defineCluster(){
		return ClusterFactory.cluster("myCluster");
	}

	@BeforeMethod
	public void createCluster(){
		new ClusterRepository(getSession()).create(defineCluster());
	}

	/* (non-Javadoc)
	 * @see com.redhat.qe.storageconsole.helpers.fixtures.ExternalFixtureTestBase#setupData(com.redhat.qe.storageconsole.helpers.fixtures.RepositoryContainer)
	 */
	@Override
	public void setupData(RepositoryContainer repos) throws TestEnvironmentConfigException {
	}
}
