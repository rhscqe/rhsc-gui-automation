/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import java.util.ArrayList;

import com.redhat.qe.helpers.MountedVolume;
import com.redhat.qe.helpers.rebalance.PopulateEachBrickStrategy;
import com.redhat.qe.helpers.rebalance.VolumePopulationStrategy;
import com.redhat.qe.model.Brick;
import com.redhat.qe.repository.rest.BrickRepository;

/**
 * @author dustin 
 * Mar 19, 2014
 */
public class RemoveBrickTestBase extends GuiPopulatedVolumeTestBase{

	@Override
	protected VolumePopulationStrategy getVolumePopulationStrategy( MountedVolume mountedVolume) {
		ArrayList<Brick> bricks = new BrickRepository(getSession(), getCreateCluster(), getCreatedVolume()).list();
		return new PopulateEachBrickStrategy(bricks, new GuiConfiguredHosts().getConfiguredHosts(), mountedVolume);
	}

}
