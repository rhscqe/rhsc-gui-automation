/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import org.testng.Assert;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.pages.components.TreeNode;
import com.redhat.qe.storageconsole.mappper.Tag;
/**
 * @author dustin 
 * Aug 8, 2013
 */
public class AssignTagDialog extends Dialog{

	private static final String ASSIGN_TAGS = "Assign Tags";

	public AssignTagDialog( Browser browser) {
		this(ASSIGN_TAGS, browser);
	}

	public AssignTagDialog(String title, Browser browser) {
		super(title, browser);
	}
	
	public void checkItemInTagList(Tag tag){
	    TreeNode tagSelection = getTagItemElement(tag);
        tagSelection.getCheckBox().toElementStub(getBrowser()).check();
	}

	public void uncheckItemInTagList(Tag tag){
		TreeNode tagSelection = getTagItemElement(tag);
		tagSelection.getCheckBox().toElementStub(getBrowser()).uncheck();
	}

	/**
	 * @param tag
	 * @return
	 */
	private TreeNode getTagItemElement(Tag tag) {
		return getTagList().getFirstChildWithName(tag.getName());
	}

	public TreeNode getTagList(){
		return new TreeNode(new JQuery("div:has(> div > div > span:contains('Root')):eq(1)"), getBrowser());
	}
	
	
	/*
	 * Precondition: tags dialog is open
	 * Postcondition: tag dialog is closed
	 */
	public void assignTags(Tag... tags){
        AssignTagDialog dialog = new AssignTagDialog(getBrowser());
        dialog.waitUntilVisible();
        dialog.getTagList().openAllTags();

		for (Tag tag : tags) {
			dialog.checkItemInTagList(tag);
		}

        dialog.getOkButton().getElementStub().click();
        Assert.assertTrue(dialog.waitUntilNotVisible(), "assign tag dialog did close");
	}

}
