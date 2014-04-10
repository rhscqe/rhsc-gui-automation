/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.helpers.Duration;
import com.redhat.qe.storageconsole.helpers.Times;
import com.redhat.qe.storageconsole.helpers.cli.CliVolume;
import com.redhat.qe.storageconsole.helpers.ssh.SshResult;
import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.sahi.base.SSHClient;
import com.redhat.qe.storageconsole.sahi.tasks.StorageCLITasks;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiVolumeTasks;
import com.redhat.qe.storageconsole.te.ClientMachine;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironment;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;
import com.redhat.qe.tools.SSHCommandResult;

/**
 * @author dustin 
 * Jan 16, 2013
 */
public class CifsMountVolumeOnCreationTest extends MountVolumeTestBase {

	
	@BeforeMethod
	public void setup() throws IOException, TestEnvironmentConfigException, JAXBException{
		super.setup(volumeWithCifsEnabledData());	
	}
	
	/**
	 * @return 
	 * 
	 */
	protected VolumeMap volumeWithCifsEnabledData() {
		VolumeMap volMap = new VolumeMap();
		volMap.setResourceLocation("System->System->Volumes");
		volMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volMap.setSpecialCount(0);
		volMap.setClusterName("automation_cluster1");
		volMap.setVolumeName("automation-volume-distribute-cifs-tests");
		volMap.setServers("{server23=bricks-distribute-6}{server24=bricks-distribute-6}");
		volMap.setCifsEnabled(true);
		return volMap;
	}
	
	@Test
	public void validateCliVolumeOption() throws IOException, TestEnvironmentConfigException, JAXBException{
		CliVolume volume = new StorageCLITasks().getVolumeInfo(getVolumeMap().getVolumeName(), getGlusterNode());
		assertEquals(volume.getOptions().get("user.cifs"), "on", "option for volume, user.cifs, was not on");
	}
	
	@Test
	public void validateSambaMountAndSmbConf() throws IOException{
		String sambaMountDir = String.format("/mnt/samba/%s", getVolumeMap().getVolumeName());
		SSHClient sshClient = new SSHClient();
		sshClient.initConnection(getGlusterNode().getHostname(), getGlusterNode().getLogin(), getGlusterNode().getPassword());
		try{
			assertTrue(sshClient.isFileOrDirectoryExist(sambaMountDir));
			assertTrue(sshClient.grep(sambaMountDir, "/etc/samba/smb.conf"));
		}finally{
			sshClient.closeConnection();
		}
	}
	
	@Test
	public void validateMountingSmbShare() throws IOException, JAXBException, TestEnvironmentConfigException{
		SSHClient sshClient = new SSHClient();
		ClientMachine mounter = TestEnvironmentConfig.getTestEnvironemt().getClientMachines().get(0);
		sshClient.initConnection(mounter.getHostname(), mounter.getLogin(), mounter.getPassword());
		String uncPathToSambaShare = String.format("//%s/gluster-%s", getGlusterNode().getHostname(),getVolumeMap().getVolumeName()); 
		String mountPoint = String.format("%s%s", mounter.getMountPoint(),getVolumeMap().getVolumeName()); 
		try{
			assertTrue(sshClient.createFileDir(mountPoint), "unable to create directory for mount point");
			SshResult mountResult = multipleMountAttempts(sshClient, "cifs", uncPathToSambaShare, mountPoint, "guest");
			assertTrue(mountResult.isSuccessful(), "could not mount the drive on " + mounter.getHostname() + ";" + "Errors Message: " + mountResult.getStderr());
			assertTrue(sshClient.unmount(mountPoint), "unable to unmount the mounted samba share.");
		}finally{
			sshClient.closeConnection();
		}
	}




}
