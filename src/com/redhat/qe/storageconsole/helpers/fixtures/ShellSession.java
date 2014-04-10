/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import org.junit.After;
import org.junit.Before;

import com.jcraft.jsch.ChannelShell;
import com.redhat.qe.config.Configuration;
import com.redhat.qe.config.RhscConfiguration;
import com.redhat.qe.ovirt.shell.RhscShellSession;
import com.redhat.qe.ssh.ChannelSshSession;

/**
 * @author dustin 
 * Aug 26, 2013
 */
public class ShellSession {
	private ChannelSshSession session;
	private RhscShellSession shell;

	@Before
	public RhscShellSession start(Configuration config) {
		session = ChannelSshSession.fromConfiguration(config);
		session.start();
		session.openChannel();
		shell = RhscShellSession.fromConfiguration(session, config);
		shell.start();
		shell.connect();
		return shell;
	}

	/**
	 * @return the session
	 */
	public ChannelSshSession getSession() {
		return this.session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(ChannelSshSession session) {
		this.session = session;
	}

	/**
	 * @return the shell
	 */
	public RhscShellSession getShell() {
		return this.shell;
	}

	/**
	 * @param shell the shell to set
	 */
	public void setShell(RhscShellSession shell) {
		this.shell = shell;
	}

	@After
	public void stop() {
		if (session != null){
			session.stopChannel();
			session.stop();
		}
	}

}
