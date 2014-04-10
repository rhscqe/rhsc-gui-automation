/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.rebalance;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.redhat.qe.helpers.MountedVolume;
import com.redhat.qe.helpers.rebalance.EmptyVolumePopulationStrategy;
import com.redhat.qe.helpers.rebalance.SimpleVolumePopulationStrategy;
import com.redhat.qe.helpers.rebalance.VolumePopulationStrategy;
import com.redhat.qe.model.WaitUtil;
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
public class SimpleRebalanceTest extends RebalanceTestBase{
	
    /**
	 * 
	 */
	private static final int NUM_ATTEMPTS_WAIT_FOR_REBALANCE_FINISH = 600;
	private static String REBALANCE_COMPLETE_IMAGE_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAABQAAAATCAYAAACQjC21AAAA7ElEQVR42mNgwAEW/1n0X38B238GagCQYeY72cAGzrnQ8Z8sA6hqGMgAEMZnWHBwJfGGgzSDDMFnGAyTbCgub5JkGLKh6C4ihAkaClS0MiSksh9E43MhLnUoIDS0XA6o4G5CQj0HiAbxKVEHsrUbaGsJiA2iQXyy1QUE1AsAJe6DaGx8UtVhtQnZJSSpc3CoZ8EWFrCwAsmTog5oQ1UYrtgCiYPkSVEH5FQcDwqqtsCmECQOkidFHZBTeQxf4gXJk6IOZHM1/hxRUU2KOmDUVygABZbiULgUJE+KOiRDK6qh3voBoSuqURQRoQ4AyppZRNn5USsAAAAASUVORK5CYII=";
	@Override
	protected VolumePopulationStrategy getVolumePopulationStrategy( MountedVolume mountedVolume) {
		return new EmptyVolumePopulationStrategy();
	}
	
	@BeforeMethod
	public void before(){
		
	}
	
	@AfterMethod
	public void afterTest(){
	
	}
	
	@Test
	public void test(){
		getBrowser().selectPage("Volumes");
		new RebalanceTasks(getBrowser()).selectFromContextMenu(getCreatedVolume(), "Rebalance");
		new RebalanceTasks(getBrowser()).openRebalanceStausDialog(getCreatedVolume());
		new RebalanceStatusDialogHelper(new RebalanceStatusDialog(getBrowser())).waitForRebalanceToFinish();
	}
	
}
