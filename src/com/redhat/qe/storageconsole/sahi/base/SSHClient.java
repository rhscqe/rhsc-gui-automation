package com.redhat.qe.storageconsole.sahi.base;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.redhat.qe.tools.SSHCommandResult;
import com.redhat.qe.tools.SSHCommandRunner;
import com.trilead.ssh2.Connection;
import com.redhat.qe.storageconsole.helpers.ssh.*; 
import com.redhat.qe.storageconsole.te.Sshable;
/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 09, 2012
 */
public class SSHClient {

	protected static Logger _logger = Logger.getLogger(SSHClient.class.getName());
	protected SSHCommandRunner sshCommandRunner = null;
	protected Connection connection = null;
	protected static final String commandCreateDir = "mkdir -vp ";
	protected static final String commandDeleteDir = "rm -rf "; 
	protected static final String commandListDir = "ls ";
	protected static final String listDiskUsage = "df -TP | sed 1d ";
	protected static final String commandUnmount = "umount ";
	protected static final String commandMount = "mount ";
	protected static final String commandStopVDSM = "service vdsmd stop";
	protected static final String commandStartVDSM = "service vdsmd start";
	protected static final String commandStopGlusterd = "service glusterd stop";
	protected static final String commandStartGlusterd = "glusterd";
	public final String commandGlusterPeerProbe = "gluster peer probe ";
	public final String commandGlusterPeerDetach = "gluster peer detach ";
	public final String commandGlusterPeerStatus = "gluster peer status ";
	public final String commandGetServerFingerprint = "ssh-keygen -l -f /etc/ssh/ssh_host_rsa_key.pub";
	public final String commandGlusterVolumeCreate = "gluster --mode=script volume create ";// --mode=script ensures that warnings are not presented
	public final String commandGlusterVolumeDelete = "gluster --mode=script volume delete ";
	public final String commandGlusterVolumeStart = "gluster --mode=script volume start ";
	public final String commandGlusterVolumeStop = "gluster --mode=script volume stop ";
	public final String commandGlusterVolumeOptionSet = "gluster --mode=script volume set ";
	public final String commandGlusterVolumeOptionReset = "gluster --mode=script volume reset ";
	public final String commandGlusterSHDProcessID = "ps -ef | grep -v grep |  grep glustershd | awk '{print $2}'";
	public final String commandGlusterNFSProcessID = "ps -ef | grep -v grep |  grep \"gluster/nfs\" | awk '{print $2}'";
	public final String commandGlusterVolumeInfo = "gluster --mode=script volume info";
	public final String commandGLusterVolumeCreate = "gluster --mode=script volume create ";
	private final String RESTART = "shutdown -rf 0";
	private final String RESTART_JBOSSAS = "service jbossas restart";
	private final String CURL = "curl -k -v ";
	private final String HOOKTESTDIR = "/var/lib/glusterd/hooks/1/start/pre/";
	public final String commandCreateDisabledHook = "echo \"echo Disabled Test Hook\" > " + HOOKTESTDIR + "K";
	public final String commandCreateEnabledHook = "echo \"echo Enabled Test Hook\" > " + HOOKTESTDIR + "S";
	public final String commandGetHooksList = "find /var/lib/glusterd/hooks/1 " + " -type f";
	public final String commandDoesHooksFileExist = "ls " + HOOKTESTDIR;
	
	public static final String service(String serviceName, String action){ return String.format("service %s %s", serviceName, action); }
	
	protected static final long COMMAND_TIMEOUT = 1000*60*10; //Timeout for commands

	
	public void initConnection(Sshable host) throws IOException{
		initConnection(host.getHostname(), host.getLogin(), host.getPassword());
	}

	public void initConnection(String hostName, String userName, String passWord) throws IOException{
		initConnection(hostName, userName, passWord, 22);
	}
	/*
	 * Creates SSH connection with the specified remote server
	 */
	public void initConnection(String hostName, String userName, String passWord, int port) throws IOException{
		if(connection == null){
			connection = new Connection(hostName, port);		
			connection.connect();
			connection.authenticateWithPassword(userName, passWord);
			_logger.log(Level.INFO, "connected to:"  + hostName);
			sshCommandRunner = new SSHCommandRunner(connection, null);
		}
	}
	
	/*
	 * Closing the existing SSH connection
	 */
	public void closeConnection(){
		if(connection != null){
			connection.close();
			connection = null;
			sshCommandRunner = null;
		}
	}

	/*
	 * Executes the specified command and returns execution status in boolean.
	 * If the terminal hangs on the specified command, waits till the specified commandTimeout time (milliseconds)
	 */
	protected boolean runCommnad(String command, long commandTimeout){
		runCommandGeneric(command, commandTimeout);
		if(sshCommandRunner.getExitCode() != 0){
			return false;
		}
		return true;
	}

	protected SshResult runCommand(String command, long commandTimeout){
		_logger.log(Level.INFO, "running command: "+ command);
		return SshResult.from(sshCommandRunner.runCommandAndWait(command, commandTimeout));
	}
	/*
	 * Executes the specified command and returns execution stderr + stdout OR null if command error.
	 * If the terminal hangs on the specified command, waits till the specified commandTimeout time (milliseconds)
	 */
	protected String runCommnadReturnOutput(String command, long commandTimeout) {
		runCommandGeneric(command, commandTimeout);
		if(sshCommandRunner.getExitCode() != 0){
			_logger.log(Level.WARNING, "Command["+command+"] Exit Code: " + sshCommandRunner.getExitCode());
			return null;
		}
		String outputString = String.format("stderr:%s::stdout:%s", sshCommandRunner.getStderr(),sshCommandRunner.getStdout());
		return (outputString);
	}

	private void runCommandGeneric(String command, long commandTimeout) {
		sshCommandRunner.runCommandAndWait(command, commandTimeout);
		_logger.log(Level.INFO, "Command["+command+"] Result (Stdout): \n"+sshCommandRunner.getStdout());
		String error = sshCommandRunner.getStderr();
		if(error.length() > 0){
			_logger.log(Level.WARNING, "Command["+command+"] Error Message (Stderr): \n"+sshCommandRunner.getStderr());
		}
	}
	
	/*
	 * Creates file or directory
	 */
	public boolean createFileDir(String dirNamefullPath){
		return runCommnad(commandCreateDir+dirNamefullPath, COMMAND_TIMEOUT);
	}	

	/*
	 * Creates parent directory
	 */
	public boolean createParentDirs(String dir){
		if(dir.endsWith("/")){
			dir = dir.substring(0, dir.lastIndexOf("/"));
		}
		dir = dir.substring(0, dir.lastIndexOf("/"));

		if(!createFileDir(dir)){
			return false;
		}
		return true;
	}

	/*
	 * Deletes the specified directory
	 */
	public boolean deleteFilesDirs(String fileDir){
		return runCommnad(commandDeleteDir+fileDir, COMMAND_TIMEOUT);
	}	
	
	public boolean getDiskUsage(){
		return this.getDiskUsage("");
	}
	public boolean getDiskUsage(String mountedOn){
		return runCommnad(listDiskUsage+mountedOn, COMMAND_TIMEOUT);
	}
	
	/*
	 * Unmounts a volume from the mount point
	 */
	public boolean unmount(String mountPoint){
		return runCommnad(commandUnmount+mountPoint, COMMAND_TIMEOUT);
	}
	
	/*
	 * Mounts a volume at the mount point
	 */
	public SshResult mount(String mountType, String deviceName, String mountPoint){
		return mount(mountType, deviceName, mountPoint, null);
	}
	
	public SshResult mount(String mountType, String deviceName, String mountPoint, String options){
		String typeNamedArg = (mountType == null) ? "" : String.format("-t %s",mountType);
		String optionsNamedArg = (options==null) ? "" : String.format("-o %s", options);
		return runCommand(String.format("%s%s %s %s %s", commandMount, typeNamedArg,deviceName, optionsNamedArg,mountPoint), COMMAND_TIMEOUT);
	}
	
	/**
	 * @param path
	 * @return
	 */
	public boolean isFileOrDirectoryExist(String path) {
		return this.runCommnad(commandListDir+path, COMMAND_TIMEOUT);
	}
	
	/**
	 * @param path
	 * @return
	 */
	public boolean grep(String pattern, String path) {
		return this.runCommnad(String.format("grep -q %s %s", pattern, path), COMMAND_TIMEOUT);
	}
	
	public boolean restart(){
		return runCommnad(RESTART, COMMAND_TIMEOUT);
	}
	
	public boolean restartJboss(){
		return runCommnad(RESTART_JBOSSAS, COMMAND_TIMEOUT);
	}
	
	public String curl(String url){
		return runCommnadReturnOutput(CURL + url, COMMAND_TIMEOUT);
	}

	public SshResult stopVdsm(){
		return runCommand(service("vdsmd", "stop"), COMMAND_TIMEOUT);
	}
	public SshResult vdsmStatus(){
		return runCommand(service("vdsmd", "status"), COMMAND_TIMEOUT);
	}
	
	public SshResult startVdsm(){
		return runCommand(service("vdsmd", "start"), COMMAND_TIMEOUT);
	}

}
