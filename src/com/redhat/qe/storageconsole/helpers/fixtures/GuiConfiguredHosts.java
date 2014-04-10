/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.redhat.qe.config.ConfiguredHosts;
import com.redhat.qe.helpers.utils.CollectionUtils;
import com.redhat.qe.helpers.utils.ListUtil;
import com.redhat.qe.model.Host;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 * @author dustin 
 * Mar 18, 2014
 */
public class GuiConfiguredHosts {
	
	public ConfiguredHosts getConfiguredHosts(){
		return new ConfiguredHosts(getHosts());
	}

	public List<Host> getHosts(){
		List<Host> results = new ArrayList<Host>();
		for ( Server server: TestEnvironmentConfig.getTestEnvironment().getServers()){
			results.add(server.toHost());
		}
		return results;
	}
	

}
