/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.cli;

import static com.redhat.qe.storageconsole.helpers.AssertUtil.failTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.mappper.VolumeMap.EachBrickAction;
import com.redhat.qe.storageconsole.sahi.tasks.StorageCLITasks;
import com.redhat.qe.storageconsole.te.Brick;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Aug 27, 2013
 */
public class BrickHelper {
	//--------------------------------create-----------------------------------------------------
	// Create directory for bricks
	//-------------------------------------------------------------------------------------
	
	public boolean deleteDirForBricks(VolumeMap volumeMap) {
		volumeMap.forEachBrick(new EachBrickAction() {
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				try {
					new StorageCLITasks().deleteBrick(server, brick);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}				
			}
		});
		
		
		return true;					
		
	}

	public boolean createDirForBricks(VolumeMap volumeMap) {
		volumeMap.forEachBrick(new EachBrickAction() {
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				try {
					new StorageCLITasks().createDir(server, brick.getLocation());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}				
			}
		});
		
		
		return true;					
		
	}
}
