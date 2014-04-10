/**
 *
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import org.testng.Assert;

import com.google.common.base.Joiner;
import com.redhat.qe.storageconsole.helpers.cli.CliVolume;
import com.redhat.qe.storageconsole.helpers.ssh.SshResult;
import com.redhat.qe.storageconsole.mappper.DiskSpaceUsageMap;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap.EachBrickAction;
import com.redhat.qe.storageconsole.mappper.VolumeMap.VolumeType;
import com.redhat.qe.storageconsole.sahi.base.SSHClient;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.te.Brick;
import com.redhat.qe.storageconsole.te.ClientMachine;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.Sshable;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 9, 2012
 */
public class StorageCLITasks extends SSHClient{

	/*
	 * Disconnect old session if any, and create new session with given details
	 */
	public void makeSSHConnection(String hostName, String userName, String password) throws IOException{
		this.closeConnection();
		this.initConnection(hostName, userName, password);
	}

	/*
	 * Validates brick(s) existence status on the specified location.
	 * If you pass 'exists' as 'true', this method returns TRUE if specified brick(s) are exists other wise returns FALSE.
	 * If you pass 'exists' as 'false', this method returns TRUE if specified brick(s) are not exists other wise returns FALSE.
	 */
	public boolean areBricksExists(List<Server> servers, String bricksCSV, boolean exists) throws IOException, TestEnvironmentConfigException{
		boolean brickStatus = exists;
		for(Server server : servers){
			this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
			for(String brickName : bricksCSV.split(",")){
				for(Brick brick : server.getBricks(brickName)){
					if(!this.runCommnad(commandListDir+brick.getLocation(), COMMAND_TIMEOUT)){
						if(exists){
							_logger.log(Level.WARNING, "Brick ["+brick.getLocation()+"] is not available on the server ["+server.getHostname()+"]");
						}
						brickStatus = false;
					}else if(!exists){
						_logger.log(Level.WARNING, "Brick ["+brick.getLocation()+"] is available on the server ["+server.getHostname()+"]");
						brickStatus = true;
					}
				}
			}
		}
		return brickStatus;
	}

	
	public SshResult createDir(Sshable server, String path) throws IOException{
		makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommand("mkdir -p " + path, COMMAND_TIMEOUT );
	}

	public void deleteBrick(Sshable server, Brick brick) throws IOException, TestEnvironmentConfigException{
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		Assert.assertTrue(this.deleteFilesDirs(brick.getLocation()), "failed to removed brick dir");
	}
		
		
	/*
	 * Creates Parent directory for the specified brick(s) on the specified server location.
	 * Returns TRUE if everything goes fine. Otherwise returns FALSE.
	 */
	public boolean createDeleteBricksDirs(Server server, String bricksCSV, boolean createDir) throws IOException, TestEnvironmentConfigException{
		boolean status = true;
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		for(String brickName : bricksCSV.split(",")){
			for(Brick brick : server.getBricks(brickName.trim())){
				if(createDir){
					if(!this.createParentDirs(brick.getLocation())){
						_logger.log(Level.WARNING, "Unable to create/add Brick parent directory ["+brick.getLocation()+"] on the server ["+server.getHostname()+"]");
						status = false;
					}
				}else{
					if(!this.deleteFilesDirs(brick.getLocation())){
						_logger.log(Level.WARNING, "Unable to delete/remove Brick Location ["+brick.getLocation()+"] on the server ["+server.getHostname()+"]");
						status = false;
					}
				}

			}
		}
		return status;
	}
	
	public boolean deleteBricksDirs(Server server, String bricksCSV) throws IOException, TestEnvironmentConfigException {
		return createDeleteBricksDirs(server, bricksCSV, false);
	}

	/*
	 * Returns usage details based on 'mount on' reference.
	 */
	public DiskSpaceUsageMap getDiskSpaceUsage(ClientMachine clientMachine, String mountedOn) throws IOException{
		ArrayList<DiskSpaceUsageMap> diskUsageDetails = this.getDiskSpaceUsages(clientMachine);
		for(DiskSpaceUsageMap diskSpaceUsageMap : diskUsageDetails){
			if(diskSpaceUsageMap.getMountedOn().equals(mountedOn)){
				return diskSpaceUsageMap;
			}
		}
		_logger.log(Level.WARNING, "Failed to get disk usage details on ["+clientMachine.getHostname()+", mounted On: "+mountedOn+"]");
		return null;
	}

	/*
	 * Get Disk Usage details as a list - Command used 'df'
	 */
	public ArrayList<DiskSpaceUsageMap> getDiskSpaceUsages(ClientMachine clientMachine) throws IOException{
		this.makeSSHConnection(clientMachine.getHostname(), clientMachine.getLogin(), clientMachine.getPassword());
		ArrayList<DiskSpaceUsageMap> diskUsageDetails = new ArrayList<DiskSpaceUsageMap>();
		if(!this.getDiskUsage()){
			_logger.log(Level.WARNING, "Failed to get disk usage details for ["+clientMachine.getHostname()+"]");
		}else{
			String[] usageDetails = this.sshCommandRunner.getStdout().split("\\n");
			DiskSpaceUsageMap diskSpaceUsageMap = null;
			for(String usageDetail : usageDetails){
				String[] subValues = usageDetail.split("\\s+");
				diskSpaceUsageMap = new DiskSpaceUsageMap();
				diskSpaceUsageMap.setFileSystem(subValues[0].trim());
				diskSpaceUsageMap.setType(subValues[1].trim());
				diskSpaceUsageMap.setSizeTotal(Long.parseLong(subValues[2].trim()));
				diskSpaceUsageMap.setSizeUsed(Long.parseLong(subValues[3].trim()));
				diskSpaceUsageMap.setSizeAvailable(Long.parseLong(subValues[4].trim()));
				diskSpaceUsageMap.setSizeUsedInPercentage(Integer.parseInt(subValues[5].trim().replace("%", "")));
				diskSpaceUsageMap.setMountedOn(subValues[6].trim());
				diskUsageDetails.add(diskSpaceUsageMap);
			}
		}
		return diskUsageDetails;
	}

public boolean mountVolume(VolumeMap volumeMap, String mountPoint, Boolean isCreateMountPoint) throws IOException, TestEnvironmentConfigException, JAXBException{

		//this.closeConnection();
		this.makeSSHConnection(volumeMap.getClientMachines().get(0).getHostname(), volumeMap.getClientMachines().get(0).getLogin(), volumeMap.getClientMachines().get(0).getPassword());
		//this.initConnection(volumeMap.getClientMachines().get(0).getHostname(), volumeMap.getClientMachines().get(0).getLogin(), volumeMap.getClientMachines().get(0).getPassword());
		DiskSpaceUsageMap diskSpaceUsageMap = new DiskSpaceUsageMap();
		String mountType = "glusterfs ";
		StorageBrowser storageSahiTasks = SahiTestBase.getStorageSahiTasks();
		HashMap<String, String> serversBricks = storageSahiTasks.getListMapFromString(volumeMap.getServers());
		List<Server> servers = storageSahiTasks.getServers(serversBricks.keySet().toString().replaceAll("[\\[\\]]", ""));
		String deviceName = servers.get(0).getHostname()+":/"+volumeMap.getVolumeName();// Prefixing hostname of machine to volume name
		unmount(mountPoint);// Unmounting from the mount point if any volume is already mounted

		if(isCreateMountPoint){
				Assert.assertTrue(createFileDir(mountPoint), "could not create mount point directory");
		}

		if(!mount(mountType, deviceName, mountPoint).isSuccessful()){// Mount the volume at the mount point
			return false;
		}

		diskSpaceUsageMap = getDiskSpaceUsage(volumeMap.getClientMachines().get(0), mountPoint);
		if(!diskSpaceUsageMap.getFileSystem().equals(deviceName)){// Checks whether volume has mounted correctly.
				_logger.log(Level.WARNING, "Volume ["+volumeMap.getVolumeName()+"] not mounted at the mount point ["
						+mountPoint+"] on machine ["+volumeMap.getClientMachines().get(0).getHostname()+"]");
				return false;
		}
		
		return true;

	}

	public boolean bringHostDown(Sshable server) throws IOException{
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommnad(commandStopVDSM, COMMAND_TIMEOUT);
	}

	public boolean bringHostUp(Sshable server) throws IOException{
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommnad(commandStartVDSM, COMMAND_TIMEOUT);
	}

	public boolean stopGlusterd(Sshable server) throws IOException{
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommnad(commandStopGlusterd, COMMAND_TIMEOUT);
	}

	public boolean startGlusterd(Sshable server) throws IOException{
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommnad(commandStartGlusterd, COMMAND_TIMEOUT);
	}

	public String runGenericCommand(String hostName, String userName, String password, String command) throws IOException {
		_logger.log(Level.INFO, "Command to run: ["+command+"]");
		this.makeSSHConnection(hostName, userName, password);
		return runCommnadReturnOutput(command, COMMAND_TIMEOUT);
	}
	
	public String runGenericCommand(Sshable server,  String command) throws IOException{
		return runGenericCommand(server.getHostname(), server.getLogin(), server.getPassword(), command);
	}
	
	
	private String glusterCreateVolumeCommandWithForce(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		return glusterCreateVolumeCommand(volumeMap) + " force";
	}
	private String glusterCreateVolumeCommand(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		final ArrayList<String> bricks = new ArrayList<String>();
		volumeMap.forEachBrick(new EachBrickAction() {
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				bricks.add(server.getHostname() + ":" + brick.getLocation());
			}
		});
		
		if(volumeMap.getVolumeType().equals(VolumeType.DISTRIBUTE.toString())){
			return String.format(commandGlusterVolumeCreate + "%s %s", volumeMap.getVolumeName(), Joiner.on(" ").join(bricks));
		} else if (volumeMap.getVolumeType().contains("Replicate")){
			return String.format(commandGlusterVolumeCreate + "%s %s %s %s", volumeMap.getVolumeName(), "replica", volumeMap.getSpecialCount(), Joiner.on(" ").join(bricks));
		} else if (volumeMap.getVolumeType().contains("Stripe")){
			return String.format(commandGlusterVolumeCreate + "%s %s %s %s", volumeMap.getVolumeName(), "stripe", volumeMap.getSpecialCount(), Joiner.on(" ").join(bricks));
		}
		throw new RuntimeException("failed to produce gluster volume create command");
		
	}
	public boolean glusterCreateVolume(VolumeMap volumeMap,Sshable serverToRunCommandOn) throws TestEnvironmentConfigException, IOException, JAXBException{
		deleteBrickDirs(volumeMap);
		createBrickDirs(volumeMap);
		this.makeSSHConnection(serverToRunCommandOn.getHostname(), serverToRunCommandOn.getLogin(),serverToRunCommandOn.getPassword());
		return runCommnad( glusterCreateVolumeCommandWithForce(volumeMap), COMMAND_TIMEOUT);
	}
	
	private void deleteBrickDirs(VolumeMap volumeMap) throws FileNotFoundException, TestEnvironmentConfigException, IOException, JAXBException{
		volumeMap.forEachBrick(new EachBrickAction() {
			
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				try{ 
					makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());				
				}catch(IOException e){
					throw new RuntimeException(e);
				}
				Assert.assertTrue(deleteFilesDirs(brick.getLocation()), "Unable to delete/remove Brick Location ["+brick.getLocation()+"] on the server ["+server.getHostname()+"]");
			}
		});
		
	}
	private void createBrickDirs(VolumeMap volumeMap) {
		volumeMap.forEachBrick(new EachBrickAction() {
			
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				try{ 
					makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());				
				}catch(IOException e){
					throw new RuntimeException(e);
				}
				Assert.assertTrue(createFileDir(brick.getLocation()), "Unable to create Brick Location ["+brick.getLocation()+"] on the server ["+server.getHostname()+"]");
			}
		});
		
	}
	public boolean glusterCreateVolume(VolumeMap volumeMap) throws TestEnvironmentConfigException, IOException{
		final ArrayList<String> serverBricks = new ArrayList<String>();
		List<Server> servers = volumeMap.getConfiguredServers();
		try {
			deleteBrickDirs(volumeMap);
			createBrickDirs(volumeMap);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		volumeMap.forEachBrick(new EachBrickAction() {
			
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				serverBricks.add(server.getHostname() + ":" + brick.getLocation());
			}
		});

		String volType = volumeType(volumeMap);
		String count = (volumeMap.getSpecialCount() <= 0  ) ? "" : volumeMap.getSpecialCount() +"";
		
		String command = String.format("%s%s %s %s %s force", commandGlusterVolumeCreate,volumeMap.getVolumeName(), volType, count, Joiner.on(" ").join(serverBricks));

		this.makeSSHConnection(servers.get(0).getHostname(), servers.get(0).getLogin(), servers.get(0).getPassword());
		return runCommnad(command , COMMAND_TIMEOUT);
	}

	/**
	 * @param volumeMap
	 * @return 
	 */
	private String volumeType(VolumeMap volumeMap) {
		String volType = null;
		if(volumeMap.getVolumeType().equals(VolumeType.DISTRIBUTE.toString())){
			volType = "";
		} else if (volumeMap.getVolumeType().contains("Replicate")){
			volType = "replica";
		} else if (volumeMap.getVolumeType().contains("Stripe")){
			volType = "stripe";
		}
		return volType;
	}

	public boolean glusterDeleteVolume(VolumeMap volumeMap, Sshable server) throws IOException{
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommnad(commandGlusterVolumeDelete + volumeMap.getVolumeName(), COMMAND_TIMEOUT);
	}

	/**
	 * @param volumeMap
	 * @param server
	 * @return
	 * @throws IOException
	 */
	public boolean glusterStartVolume(VolumeMap volumeMap, Sshable server) throws IOException {
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommnad(commandGlusterVolumeStart + volumeMap.getVolumeName(), COMMAND_TIMEOUT);
	}

	/**
	 * @param volumeMap
	 * @param server
	 * @return
	 * @throws IOException
	 */
	public boolean glusterStopVolume(VolumeMap volumeMap, Sshable server) throws IOException {
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommnad(commandGlusterVolumeStop + volumeMap.getVolumeName(), COMMAND_TIMEOUT);
	}

	/**
	 * @param volumeMap
	 * @param server
	 * @return
	 * @throws IOException
	 */
	public boolean glusterVolumeOptionSet(String volumeName, String optionName, String optionValue, Sshable server) throws IOException {
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommnad(commandGlusterVolumeOptionSet + volumeName + " " + optionName + " " + "\"" +  optionValue + "\"", COMMAND_TIMEOUT);
	}

	/**
	 * @param volumeMap
	 * @param server
	 * @return
	 * @throws IOException
	 */
	public boolean glusterVolumeOptionReset(String volumeName, String optionName, Sshable server) throws IOException {
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommnad(commandGlusterVolumeOptionReset + volumeName + " " + optionName, COMMAND_TIMEOUT);
	}

	/**
	 * @param volumeMap
	 * @param server
	 * @return
	 * @throws IOException
	 */
	public boolean glusterVolumeOptionResetAll(String volumeName, Sshable server) throws IOException {
		this.makeSSHConnection(server.getHostname(), server.getLogin(), server.getPassword());
		return runCommnad(commandGlusterVolumeOptionReset + volumeName, COMMAND_TIMEOUT);
	}
	
    public String getServerGlusterNFSProcessID(String ip, String userName, String password) throws IOException {
    	return getProcessId(ip, userName, password, commandGlusterNFSProcessID);
    }

    public String getServerGlusterSHDProcessID(String ip, String userName, String password) throws IOException {
    	return getProcessId(ip, userName, password, commandGlusterSHDProcessID);
    }

    private String getProcessId(String ip, String userName, String password, String command) throws IOException {
        this.makeSSHConnection(ip, userName, password);
        String commandOutput = runGenericCommand(ip, userName, password, command);
        this.closeConnection();
        return commandOutput.replaceAll("[^\\d]", "");
    }

	public String getVolumeInfoRaw(String volumeName, Sshable server) throws IOException{
		String command = commandGlusterVolumeInfo;
		if(volumeName != null){
			command = command + " " + volumeName;
		}
		return runGenericCommand(server, command);
	}
	
	public List<CliVolume> getVolumeInfo(Sshable server) throws IOException{
		return CliVolume.parse(getVolumeInfoRaw(null, server));
	}
	public CliVolume getVolumeInfo(String volumeName, Sshable server) throws IOException{
		return CliVolume.parse(getVolumeInfoRaw(volumeName, server)).get(0);
	}

	public boolean createDisabledHook(ServerMap server, String fileName) throws IOException {
		return commandHandler(commandCreateDisabledHook + fileName, server.getServerHostIP(), server.getServerUsername(), server.getServerPassword());
	}

	public boolean createEnabledHook(ServerMap server, String fileName) throws IOException {
		return commandHandler(commandCreateEnabledHook + fileName, server.getServerHostIP(), server.getServerUsername(), server.getServerPassword());
	}
	
	public ArrayList<String> getHooksList(ServerMap server) throws IOException {
		ArrayList<String> result = new ArrayList<String>();
		String[] files = getFilesInHookDir(server);
		for(String file: files){
			String fileName = file.substring(file.lastIndexOf("/") + 1).trim();
			if(fileName.matches("^[SK].*")){
				result.add(fileName.replaceAll("^[a-zA-Z]", ""));
			}
		}
		return result;
	}

	private String[] getFilesInHookDir(ServerMap server) throws IOException {
		String returnString = new String();
		makeSSHConnection(server.getServerHostIP(), server.getServerUsername(), server.getServerPassword());
		try {
			returnString = runGenericCommand(server.getServerHostIP(), server.getServerUsername(), server.getServerPassword(), commandGetHooksList);
		} finally {
			closeConnection();
		}
		
		return returnString.split("\n");
	}
	
	public boolean doesFileExist(ServerMap server, String fileName) throws IOException {
		return commandHandler(commandDoesHooksFileExist + "*" + fileName, server.getServerHostIP(), server.getServerUsername(), server.getServerPassword());
	}
	
	private boolean commandHandler(String command, String host, String userName, String password) throws IOException {
		boolean result = true;
		makeSSHConnection(host, userName, password);
		try {
			result = runCommnad(command, COMMAND_TIMEOUT);
		} finally {
			closeConnection();
		}
		return result;
	}
	
}
