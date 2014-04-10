/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.google.common.base.Function;
import com.redhat.qe.storageconsole.helpers.CannotStartConnectException;
import com.redhat.qe.storageconsole.helpers.cli.Ssher;
import com.redhat.qe.storageconsole.sahi.base.SSHClient;
import com.redhat.qe.storageconsole.te.Sshable;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Jan 24, 2013
 */
public class RhscHostTasks {
	private static Logger LOG = Logger.getLogger(RhscHostTasks.class.getName());
	private Sshable host;

	public void restart() throws CannotStartConnectException{
		runCommand(new Function<SSHClient, Boolean>() {
			public Boolean apply(SSHClient client){
				client.restart();
				return true;
			}
		});
	}
	public void restartJboss() throws CannotStartConnectException{
		runCommand(new Function<SSHClient, Boolean>() {
			public Boolean apply(SSHClient client){
				client.restartJboss();
				return true;
			}
		});
	}
	public String curl(final String url) throws CannotStartConnectException{
		return runCommand(new Function<SSHClient, String>() {
			public String apply(SSHClient client){
				LOG.log(Level.INFO, "running curl comand to " + url);
				return client.curl(url);
			}
		});
	}
	
	public <O> O runCommand(Function<SSHClient,O> commands) throws CannotStartConnectException{
		return new Ssher().runCommand(commands, getHost());
	}
	/**
	 * @return
	 */
	private Sshable getHost() {
		this.host = host == null ? getRhsHost() : host;
		return host;
	}

	/**
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws TestEnvironmentConfigException
	 */
	private Sshable getRhsHost()  {
		try {
			return TestEnvironmentConfig.getTestEnvironemt().getRhscHost();
		} catch (Exception e) {
			throw new RuntimeException("unable to get test config",e);
		}
	}
	/**
	 * @return
	 */
	public boolean waitUntilSeverStopsResponding() {
		return new Ssher().waitUntilSeverStopsResponding(getHost());
	}

}
