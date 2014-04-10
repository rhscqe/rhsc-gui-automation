/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.rebalance;

import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.helpers.MountedVolume;
import com.redhat.qe.helpers.rebalance.EmptyVolumePopulationStrategy;
import com.redhat.qe.helpers.rebalance.SimpleVolumePopulationStrategy;
import com.redhat.qe.helpers.rebalance.VolumePopulationStrategy;
import com.redhat.qe.helpers.utils.FileSize;
import com.redhat.qe.model.Host;
import com.redhat.qe.model.WaitUtil;
import com.redhat.qe.repository.glustercli.RebalanceStatus;
import com.redhat.qe.repository.glustercli.VolumeRebalanceRepository;
import com.redhat.qe.ssh.ExecSshSession;
import com.redhat.qe.storageconsole.helpers.GlusterCliRebalanceStatusVerifier;
import com.redhat.qe.storageconsole.helpers.elements.Row;
import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.elements.volume.RebalanceStatusDialog;
import com.redhat.qe.storageconsole.helpers.fixtures.RebalanceTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.RebalanceStatusDialogHelper;
import com.redhat.qe.storageconsole.sahi.tasks.RebalanceTasks;

import dstywho.functional.Predicate;

/**
 * @author dustin 
 * Mar 11, 2014
 */
@Test
public class RebalanceDistVolumeTest extends RebalanceTestBase{
	
    /**
	 * 
	 */
	@Override
	protected VolumePopulationStrategy getVolumePopulationStrategy( MountedVolume mountedVolume) {
		SimpleVolumePopulationStrategy strategy = new SimpleVolumePopulationStrategy(mountedVolume); //TODO
		strategy.setMaxSizeToWrite(FileSize.Gigabytes(5));
		return strategy;
	}
	
	@BeforeMethod
	public void before(){
		
	}
	
	@AfterMethod
	public void afterTest(){
	
	}
	
	@Test
	@Tcms("304835")
	public void test(){
		getBrowser().selectPage("Volumes");
		new RebalanceTasks(getBrowser()).selectFromContextMenu(getCreatedVolume(), "Rebalance");
		new RebalanceTasks(getBrowser()).openRebalanceStausDialog(getCreatedVolume());
		new RebalanceStatusDialogHelper(new RebalanceStatusDialog(getBrowser())).waitForRebalanceToFinish();

		ArrayList<RebalanceStatus> actual = new RebalanceStatusDialog(getBrowser()).getTable().getStatus();
		Host masterHost = defineServers()[0].toHost();
		ArrayList<RebalanceStatus> expected = getGlusterRebalanceStatus(masterHost);
		new GlusterCliRebalanceStatusVerifier(masterHost,expected, actual).verify();;

	}
	private ArrayList<RebalanceStatus> getGlusterRebalanceStatus(Host host) {
		ExecSshSession masterNodeSshSession = ExecSshSession.fromHost(host);
		masterNodeSshSession.start();
		try{
			VolumeRebalanceRepository glusterRebalanceCli = new VolumeRebalanceRepository(masterNodeSshSession);
			return  glusterRebalanceCli.getRebalanceStatus(getCreatedVolume());
		}finally{
			masterNodeSshSession.stop();
		}
	}
	
}
