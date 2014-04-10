/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.cli;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.redhat.qe.storageconsole.helpers.CannotStartConnectException;
import com.redhat.qe.storageconsole.helpers.Duration;
import com.redhat.qe.storageconsole.helpers.Times;
import com.redhat.qe.storageconsole.sahi.base.SSHClient;
import com.redhat.qe.storageconsole.sahi.tasks.RhscHostTasks;
import com.redhat.qe.storageconsole.sahi.tests.server.RestartTest;
import com.redhat.qe.storageconsole.te.Sshable;

/**
 * @author dustin 
 * Aug 27, 2013
 */
public class Ssher {
	final protected static Logger LOG = Logger.getLogger(Ssher.class.getName());

	private static final int NUM_ATTEMPTS = 50;
	private static final int TWO_MINUTES_ATLEAST= 120;

	public <O> O runCommand(Function<SSHClient,O> commands, Sshable host ) throws CannotStartConnectException{
		SSHClient client = new SSHClient();
		try {
			connect(client,host);
		} catch (IOException e) {
			throw new CannotStartConnectException(e);
		}
		try{
			return commands.apply(client);
		}finally{
			client.closeConnection();
		}
		
	}
	
	private SSHClient connect(SSHClient client, Sshable host) throws IOException {
		client.initConnection(host.getHostname(), host.getLogin(), host.getPassword());
		return client;
	}
	
	public boolean waitUntilSeverStopsResponding(Sshable host){
		for(int index:new Times(NUM_ATTEMPTS)){
			try{
				LOG.log(Level.INFO, String.format("waiting for server to stop attempt %s/%s", index,NUM_ATTEMPTS));
				SSHClient client = new SSHClient();
				client.initConnection(host);
				client.closeConnection();
				Duration.ONE_SECOND.sleep();
			}catch(IOException e){
				LOG.log(Level.INFO, String.format("server stopped"));
				return true;
			}
		}
		return false;
	}
	
	public boolean waitUntilSeverStarts(Sshable host){
		for(int index:new Times(TWO_MINUTES_ATLEAST)){
				SSHClient client = new SSHClient();
			try{
				LOG.log(Level.INFO, String.format("waiting for server to start attempt %s/%s", index,TWO_MINUTES_ATLEAST));
				client.initConnection(host);
				return true;
			}catch(IOException e){
				Duration.ONE_SECOND.sleep();
				LOG.log(Level.INFO, String.format("server not responding"));
			}finally{
				client.closeConnection();
			}
		}
		return false;
	}
}
