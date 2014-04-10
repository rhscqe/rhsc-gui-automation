/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import org.testng.Assert;

import com.redhat.qe.model.IVolume;
import com.redhat.qe.model.Volume;
import com.redhat.qe.model.WaitUtil;
import com.redhat.qe.model.WaitUtil.WaitResult;
import com.redhat.qe.storageconsole.helpers.elements.Cell;
import com.redhat.qe.storageconsole.helpers.elements.ContextMenu;
import com.redhat.qe.storageconsole.helpers.elements.MainTabRefreshButton;
import com.redhat.qe.storageconsole.helpers.elements.Row;
import com.redhat.qe.storageconsole.helpers.elements.VolumeTable;
import com.redhat.qe.storageconsole.helpers.elements.volume.ActionMenu;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;

import dstywho.functional.Predicate;

/**
 * @author dustin 
 * Mar 21, 2014
 */
public class RebalanceTasks {
	private static String REBALANCE_ACTION_IMAGE_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAAA0AAAANCAYAAABy6+R8AAABWUlEQVQokZVS3UoCQRT21sfwLQxi3yEIn0CfIHqPrryKQIJA8kJaCCUJraEiSPGHtv0RRMVZzd2ULRp19DRn2dVRvOmDj909+318c86cSETCwdFJTFARTAgmgyd+xyK7EMWoYFwwRV41MrBdOl9wPvV+qGZ1CdaD/1HZFL9Wn1XNGLBu3wHH9YCxOYTgfMme3jQVdfKRUu86ZbppQ7szAjqcgOf9onjLGCTG0KQUyk2imyNAk2HZsC8NERxVQVOi1ujTdKYI4t2nnBbWcrcEsEfUoylpWi7XrTGkM4W16Cr/sGVALFcrjno/qd6i1LAcqDV6cHaursXIi+wdTKbfvklOUu6JQaz2F2Aa9naZq8Dh8Slkb8hWb3JP/vQMy2WYppufgkPYneRstthML7ynfKGm6qbDwjR5kqPxlFVempt72t2IYrlFqvUe/TBsXq13aOmxsX8j/rt7fzQdkndrnLyKAAAAAElFTkSuQmCC";
	private StorageBrowser browser;

	public RebalanceTasks(StorageBrowser browser){
		this.browser = browser;
	}

	public StorageBrowser getBrowser(){
		return browser;
	}

	public JQuery showContextMenuItem(final IVolume volume, String contextMenuItem){
		final JQuery rebalanceContextMenuItem = new ContextMenu(getBrowser()).getItem("Rebalance");
		Assert.assertTrue(WaitUtil.waitUntil(new Predicate() {
			
			@Override
			public Boolean act() {
				getBrowser().div(volume.getName()).rightClick(); 
				String classNames =  rebalanceContextMenuItem.addCall("attr","class" ).fetch(getBrowser());
				return ! classNames.contains("disabled");
			}
		}, 12).isSuccessful(), "menu item should be enabled");
		return rebalanceContextMenuItem;
	}

	public void selectFromContextMenu(final IVolume volume, String contextMenu){
		JQuery contextMenuItem = showContextMenuItem(volume, contextMenu);
		contextMenuItem.toElementStub(getBrowser()).click();
	}
	
	
	public void openRebalanceStausDialog(IVolume volume){
		selectItemFromRebalanceActivitiesMenu(volume, "Status");
	}

	public void selectItemFromRebalanceActivitiesMenu(IVolume volume, String item){
		openActivitiesMenu(volume);
		new ActionMenu(getBrowser()).getItem(item).toElementStub(getBrowser()).click();;
	}

	public void openActivitiesMenu(IVolume volume){
		Assert.assertTrue(waitForRebalanceActivitiesMenuButtonToDisplay(volume).isSuccessful(), "activities button for volume not displayed");
		getActivitiesDropdown(volume).toElementStub(getBrowser()).click();
	}
	
	public WaitResult waitForRebalanceActivitiesMenuButtonToDisplay(IVolume volume){
		final JQuery actionsDropdown = getActivitiesDropdown(volume);
		return WaitUtil.waitUntil(new Predicate() {
			
			@Override
			public Boolean act() {
				new MainTabRefreshButton( browser).click();
				return (! actionsDropdown.property("length").fetch(getBrowser()).contains("0")) && actionsDropdown.addCall("css", "background-image").fetch(getBrowser()).contains(REBALANCE_ACTION_IMAGE_BASE64);
			}
		}, 10);
	}

	private JQuery getActivitiesDropdown(IVolume volume) {
		final Cell activitiesCell = getActivitiesCellForVolume(volume);
		final JQuery actionsDropdown = activitiesCell.getJqueryObject().find("img:last");
		return actionsDropdown;
	}

	private Cell getActivitiesCellForVolume(IVolume volume) {
		int activitiesIndex = getVolumeTable().getHeaders().indexOf("Activities");
		Row volumeRow = getVolumeTable().getFirstRowThatContainsText(volume.getName());
		final Cell activitiesCell = volumeRow.getCell(activitiesIndex);
		return activitiesCell;
	}

	private VolumeTable getVolumeTable() {
		return new VolumeTable(getBrowser());
	}


	
}
