/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import java.util.ArrayList;
import java.util.List;

import com.redhat.qe.config.Configuration;
import com.redhat.qe.config.ShellHost;
import com.redhat.qe.model.Host;
import com.redhat.qe.ssh.Credentials;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.Sshable;
import com.redhat.qe.storageconsole.te.TestEnvironment;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 * @author dustin 
 * Aug 26, 2013
 */
public class ShellConfiguration {
	


	private static ShellHost getShellHost(){
		Sshable rhscHost = TestEnvironmentConfig.getTestEnvironment().getRhscHost();
		return new ShellHost(rhscHost.getHostname(), new Credentials(rhscHost.getLogin(), rhscHost.getPassword()), 22);
	}
	
	private static Configuration basicRhscConfiguration(){
		return new Configuration(RestApiConfiguration.getRestApi(),getShellHost());
	}

	public static Configuration getConfiguration() {
		Configuration config = basicRhscConfiguration();
		ArrayList<Host> hosts = getHosts();
		config.setHosts(hosts);
		return config;
	}
	
	private static ArrayList<Host> getHosts() {
		TestEnvironment testenv = TestEnvironmentConfig.getTestEnvironment();
		ArrayList<Host> hosts = new ArrayList<Host>();
		List<Server> servers = testenv.getServers();
		for(Sshable server: servers){
			Host host = new Host();
			host.setAddress(server.getHostname());
			host.setRootPassword(server.getPassword());
			hosts.add(host);
		}
		return hosts;
	}


}
