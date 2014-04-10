/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import org.testng.Assert;

import com.redhat.qe.model.WaitUtil;
import com.redhat.qe.storageconsole.helpers.jQueryPagePanels;
import com.redhat.qe.storageconsole.helpers.elements.AssignTagDialog;
import com.redhat.qe.storageconsole.helpers.elements.ContextMenu;
import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.mappper.ServerMap;
import com.redhat.qe.storageconsole.mappper.Tag;

/**
 * @author dustin 
 * Aug 16, 2013
 */
public class UsersMainTabTasks {
	private StorageBrowser browser;

	public UsersMainTabTasks(StorageBrowser browser){
		this.browser = browser;
	}
	
	public void selectUser(String firstName){
		browser.div(firstName).click();
	}
	
	public TableElement getTable(){
		return new TableElement(getJQueryTableSelector(), browser);
	}

	private JQuery getJQueryTableSelector() {
		return new jQueryPagePanels().getMainTabPanel().find("th:contains('First Name')").addCall("last").addCall("closest", "table");
	}
	
	public void navigateToTab(){
		browser.selectPage("Users");
		Assert.assertTrue(getTable().waitUntilVisible());
	}
	
    public void tagUser(String user, Tag... tag) {
    	navigateToTab();
    	selectUser(user);
        browser.div(user).rightClick();  // Click on Server
        new ContextMenu(browser).getItem("Assign Tags").toElementStub(browser).click();  


        AssignTagDialog dialog = new AssignTagDialog(browser);
        dialog.assignTags(tag);
    }


}
