/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import java.util.ArrayList;
import java.util.List;

import com.redhat.qe.config.Configuration;
import com.redhat.qe.config.RestApi;
import com.redhat.qe.config.ShellHost;
import com.redhat.qe.helpers.cleanup.CleanupTool;
import com.redhat.qe.helpers.cleanup.RestCleanupTool;
import com.redhat.qe.model.Host;
import com.redhat.qe.ssh.Credentials;
import com.redhat.qe.storageconsole.helpers.fixtures.ShellConfiguration;
import com.redhat.qe.storageconsole.te.RhscCredential;
import com.redhat.qe.storageconsole.te.RhscHost;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.Sshable;
import com.redhat.qe.storageconsole.te.TestEnvironment;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 * @author dustin 
 * Jun 3, 2013
 */
public class RhscCleanerTool {
	
	public static void main(String[] args){
		cleanup();
	}

	/**
	 * 
	 */
	public static void cleanup() {
		new RestCleanupTool().cleanup(ShellConfiguration.getConfiguration());
	}
	
	private static ShellHost getShellHost(){
		Sshable rhscHost = TestEnvironmentConfig.getTestEnvironment().getRhscHost();
		return new ShellHost(rhscHost.getHostname(), new Credentials(rhscHost.getLogin(), rhscHost.getPassword()), 22);
	}
	
	/**
	 * @return
	 */
	private static Configuration mapConfigurationsToCliConfiguratoins() {
		TestEnvironment testenv = TestEnvironmentConfig.getTestEnvironment();
		RhscCredential rhscCredentials = testenv.getRhscAdminCredentials();
		RhscHost rhscHost = testenv.getRhscHost();
		Configuration config = new Configuration(
				new RestApi("https://localhost:443/api", new Credentials(getLoginWithDomain(rhscCredentials), rhscCredentials.getPassword())) , 
				new ShellHost(rhscHost.getHostname(), new Credentials(rhscHost.getLogin(), rhscHost.getPassword()), 22));
		ArrayList<Host> hosts = serversToHosts(testenv);
		config.setHosts(hosts);
		return config;
	}

	/**
	 * @param testenv
	 * @return
	 */
	private static ArrayList<Host> serversToHosts(TestEnvironment testenv) {
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

	/**
	 * @param rhscCredentials
	 * @return
	 */
	private static String getLoginWithDomain(RhscCredential rhscCredentials) {
		return rhscCredentials.getUsername() + "@" + rhscCredentials.getDomain();
	}

}
