/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;

import com.ibatis.common.logging.Log;
import com.redhat.qe.config.ConfiguredHosts;
import com.redhat.qe.config.RhscConfiguration;
import com.redhat.qe.helpers.MountedVolume;
import com.redhat.qe.helpers.rebalance.VolumePopulationStrategy;
import com.redhat.qe.helpers.rebalance.VolumePopulator;
import com.redhat.qe.helpers.repository.RecentlyMigratedRebalancedVolumeHelper;
import com.redhat.qe.helpers.ssh.MountHelper;
import com.redhat.qe.helpers.ssh.MountedVolumeHelper;
import com.redhat.qe.helpers.utils.AbsolutePath;
import com.redhat.qe.helpers.utils.Path;
import com.redhat.qe.model.Host;
import com.redhat.qe.repository.rest.VolumeRepository;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Mar 11, 2014
 */
public abstract class GuiPopulatedVolumeTestBase extends VolumeTestBase{
	
	protected static Logger LOG = Logger.getLogger(GuiPopulatedVolumeTestBase.class.getName());

	private MountedVolume mountedVolume;



	public Host getMounter() throws TestEnvironmentConfigException{
		return new GuiConfiguredHosts().getConfiguredHosts().getHostByName(getCreatedHosts().get(0));
	}

	public AbsolutePath getMountPoint() throws TestEnvironmentConfigException{
		return new AbsolutePath( new Path("/").add("mnt", getCreatedVolume().getName()));
	}
	
	@BeforeMethod
	public void populateVolume() throws TestEnvironmentConfigException{
		Host mounter = getMounter();
		AbsolutePath mountPoint = getMountPoint();

		if(getVolumeRepository().show(getCreatedVolume()).getStatus().equals("down"))
			getVolumeRepository().start(getCreatedVolume());
		mountedVolume = mountAndPopulate(mounter, mountPoint);
	}

	private VolumeRepository getVolumeRepository() {
		return new VolumeRepository(getSession(), getCreateCluster());
	}

	MountedVolume mountAndPopulate(Host mounter, AbsolutePath mountPoint) {
		mountedVolume = MountHelper.mountVolume(mounter, mountPoint, getCreatedVolume());
		new VolumePopulator(getVolumePopulationStrategy(mountedVolume)).populate();
		return mountedVolume;
	}
	
	protected abstract VolumePopulationStrategy getVolumePopulationStrategy(MountedVolume mountedVolume);
		
	
	
	@AfterMethod(alwaysRun=true)
	public void destroyVolume() throws TestEnvironmentConfigException{
		LOG.log(Level.INFO, "Starting to teardown populated volume.");
		new MountedVolumeHelper().cleanupMountedVolume(getVolumeRepository(), mountedVolume);
		MountHelper.unmount(mountedVolume);
	
		new RecentlyMigratedRebalancedVolumeHelper().stopVolume(getVolumeRepository(), getCreatedVolume(), new GuiConfiguredHosts().getHosts());
		new RecentlyMigratedRebalancedVolumeHelper().destroyVolume(getVolumeRepository(), getCreatedVolume(),  new GuiConfiguredHosts().getHosts());
	}
	


}
