/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.rebalance;


import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.factories.BrickFactory;
import com.redhat.qe.helpers.MountedVolume;
import com.redhat.qe.helpers.rebalance.EmptyVolumePopulationStrategy;
import com.redhat.qe.helpers.rebalance.VolumePopulationStrategy;
import com.redhat.qe.model.Volume;
import com.redhat.qe.repository.rest.BrickRepository;
import com.redhat.qe.storageconsole.helpers.elements.Dialog;
import com.redhat.qe.storageconsole.helpers.elements.ErrorDialog;
import com.redhat.qe.storageconsole.helpers.fixtures.GuiVolumeFactory;
import com.redhat.qe.storageconsole.helpers.fixtures.RebalanceTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.RebalanceTasks;

/**
 * @author dustin 
 * Mar 21, 2014
 */
public class StartRebalanceWithSingleBrickDistVolume extends RebalanceTestBase{
	
	@Override
	protected VolumePopulationStrategy getVolumePopulationStrategy( MountedVolume mountedVolume) {
		return new EmptyVolumePopulationStrategy();
	}
	
	@BeforeMethod
	public void addBricksToVolume(){
		//don't add any bricks to the volume
	}
	
	@Override
	protected Volume volumeCreationStrategy() {
		Volume result = new GuiVolumeFactory().distributed("mySingleBrickDistVolume",1, getCreatedHosts());
		return result;
	}
	
	@Test
	@Tcms("301786")
	public void test(){
		getBrowser().selectPage("Volumes");
		new RebalanceTasks(getBrowser()).selectFromContextMenu(getCreatedVolume(), "Rebalance");
		Assert.assertTrue(getDialog().waitUntilVisible(), "error dialog did not display");
		Assert.assertTrue(getDialog().getText().contains("Cannot rebalance Gluster Volume. Gluster Volume has a single brick")
				,"dialog does not contain correct error text" );
	}

	private Dialog getDialog() {
		return new Dialog("Operation Canceled",getBrowser());
	}
	

}
