/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import com.redhat.qe.storageconsole.mappper.VolumeMap;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author dustin 
 * Aug 27, 2013
 */
public class VolumeMapFactory {
	
	public VolumeMap distributedVolume(String name, String clusterName){
		VolumeMap volumeMap = new VolumeMap();
		String authAllowTestValue = null;
		volumeMap.setResourceLocation("System->Volumes");
		volumeMap.setClusterName(clusterName);
		volumeMap.setVolumeName(name);
		volumeMap.setVolumeType(VolumeMap.VolumeType.DISTRIBUTE.toString());
		volumeMap.setServers("{server23=bricks-distribute-1}{server24=bricks-distribute-1}");
		volumeMap.setNfsEnabled(true);
		volumeMap.setSpecialCount(0);
		volumeMap.setClientMachines(TestEnvironmentConfig.getTestEnvironment().getClientMachines());
		volumeMap.setVolumeOptionKey(VolumeMap.optionType.DIAGNOSTICS_BRICK_SYS_LOG_LEVEL.toString());
		volumeMap.setVolumeOptionValue("ERROR");
		volumeMap.setVolumeEditOptionValue("CRITICAL");
		authAllowTestValue = getAuthAllowVal();
		volumeMap.setVolumeAuthAllowValue(authAllowTestValue);
		return volumeMap;
	}

	/**
	 * @return
	 * @throws TestEnvironmentConfigException
	 */
	private String getAuthAllowVal() {
		try {
			return TestEnvironmentConfig.getTestEnvironment().getGeneralKeyValueMapFromKey("SINGLE_IP").getValue();
		} catch (TestEnvironmentConfigException e) {
			throw new RuntimeException(e);
		}
	}

}
