/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.ssh;

import com.redhat.qe.tools.SSHCommandResult;

/**
 * @author dustin 
 * Jan 30, 2013
 */
public class SshResult extends SSHCommandResult{
	
	public static SshResult  from(SSHCommandResult from){
		return new SshResult(from.getExitCode(), from.getStdout(), from.getStderr());
	}
	

	public SshResult(Integer exitCode, String stdout, String stderr) {
		super(exitCode, stdout, stderr);
	}


	public boolean isSuccessful(){
		return (getExitCode() != 0) ? false : true;
	}
}
