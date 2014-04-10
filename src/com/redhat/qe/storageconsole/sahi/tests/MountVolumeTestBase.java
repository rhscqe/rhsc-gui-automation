/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.testng.annotations.AfterClass;

import com.redhat.qe.storageconsole.helpers.Duration;
import com.redhat.qe.storageconsole.helpers.Times;
import com.redhat.qe.storageconsole.helpers.ssh.SshResult;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.sahi.base.SSHClient;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiVolumeTasks;
import com.redhat.qe.storageconsole.te.ClientMachine;
import com.redhat.qe.storageconsole.te.Sshable;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;
import com.redhat.qe.tools.SSHCommandResult;

/**
 * @author dustin 
 * Jan 18, 2013
 */
public class MountVolumeTestBase extends SahiTestBase {
	private StorageSahiVolumeTasks tasks = null;
	private VolumeMap volumeMap;
	private Sshable glusterNode;
	/**
	 * 
	 */
	public MountVolumeTestBase() {
		super();
	}

	public void setup(VolumeMap volumeMap) throws IOException,
	TestEnvironmentConfigException, JAXBException {
		this.volumeMap = volumeMap;
		this.tasks = new StorageSahiVolumeTasks(browser);
		this.glusterNode = volumeMap.getConfiguredServers().get(0);
		assertTrue(tasks.createVolume(volumeMap).isSuccessful(), "Test setup: volume could not be created");
		assertTrue(tasks.startVolume(volumeMap), "Test setup: volume could not be started");
	}

	/**
	 * @return the tasks
	 */
	public StorageSahiVolumeTasks getTasks() {
		return this.tasks;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(StorageSahiVolumeTasks tasks) {
		this.tasks = tasks;
	}

	/**
	 * @return the volumeMap
	 */
	public VolumeMap getVolumeMap() {
		return this.volumeMap;
	}

	/**
	 * @param volumeMap the volumeMap to set
	 */
	public void setVolumeMap(VolumeMap volumeMap) {
		this.volumeMap = volumeMap;
	}

	public Sshable getGlusterNode() {
		return this.glusterNode;
	}

	public void setGlusterNode(Sshable mounter) {
		this.glusterNode = mounter;
	}

	@AfterClass
	public void teardown() {
		assertTrue(tasks.stopVolume(volumeMap), "volume could not be stopped");
		assertTrue(tasks.removeVolume(volumeMap), "volume could not be removed");
		
	}
	
	protected void sleepForASecond() {
		try {
			Thread.sleep(Duration.ONE_SECOND.toMilliseconds());
		} catch (InterruptedException e) {
			throw new RuntimeException("failed to sleep");
		}
	}
	
	protected SshResult multipleMountAttempts(SSHClient sshClient, String type, String shareLocation, String mountPoint, String option) {
		SshResult result = null;
		for(int attempt : new Times(10)){
			result = sshClient.mount(type, shareLocation, mountPoint, option);
			if(result.isSuccessful()) return result;
			sleepForASecond();
		}
		return result;
	}
	
	public void validateMountingNfsShare(boolean isSuccessful) throws IOException, JAXBException, TestEnvironmentConfigException{
		SSHClient sshClient = new SSHClient();
		ClientMachine mounter = TestEnvironmentConfig.getTestEnvironemt().getClientMachines().get(0);
		sshClient.initConnection(mounter.getHostname(), mounter.getLogin(), mounter.getPassword());
		String nfsPath = String.format("%s:%s", getGlusterNode().getHostname(), getVolumeMap().getVolumeName()); 
		String mountPoint = String.format("%s%s", mounter.getMountPoint(),getVolumeMap().getVolumeName()); 
		try{
			assertTrue(sshClient.createFileDir(mountPoint), "unable to create directory for mount point");
			SshResult mountResult = multipleMountAttempts(sshClient, "nfs", nfsPath, mountPoint, "vers=3");
			if(isSuccessful == true){
				assertTrue(mountResult.isSuccessful(), "was unable to mount nfs on " + mounter.getHostname() + "; Error message: " + mountResult.getStderr() + mountResult.getStdout());
			}else{
				assertFalse(mountResult.isSuccessful(), "was able to mount the drive on " + mounter.getHostname());
			}
			assertTrue(sshClient.unmount(mountPoint), "unable to unmount");
		}finally{
			sshClient.closeConnection();
		}
	}


}
