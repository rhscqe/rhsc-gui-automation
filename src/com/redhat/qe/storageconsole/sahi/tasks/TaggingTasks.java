/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.elements.CreateTagModal;
import com.redhat.qe.storageconsole.helpers.elements.Dialog;
import com.redhat.qe.storageconsole.helpers.elements.EditTagModal;
import com.redhat.qe.storageconsole.helpers.elements.TagModal;
import com.redhat.qe.storageconsole.helpers.pages.components.LHSAccordion;
import com.redhat.qe.storageconsole.helpers.pages.components.TreeNode;
import com.redhat.qe.storageconsole.mappper.Tag;

/**
 * @author dustin 
 * Aug 6, 2013
 */
/**
 * 
 */

/**
 * @author dustin Jul 16, 2013
 * 
 */
public class TaggingTasks {

	private StorageBrowser browser;

	public TaggingTasks(StorageBrowser browser) {
		this.browser = browser;
	}

	private static final int NUM_ATTEMPTS = 20;

	public void removeTag(Tag tag) {
		openAccordion();
		TreeNode root = getRootTagElement();
		root.openAllTags();		
		ElementStub myTag = browser.div(tag.getName()).in(getTagSection());
		if (myTag.isVisible()) {
			myTag.click();
			browser.div("Remove").in(getTagSection()).click();
			confirmRemoval();
			Assert.assertTrue(WaitUtil.waitUntil(
					new WaitUtil.ElementIsNotVisible(myTag), NUM_ATTEMPTS),
					"tag was not removed");
		}
	}


	/**
	 * @return
	 */
	private TreeNode getRootTagElement() {
		return TreeNode.getNodeByName(browser, "Root");
	}
	

	/*
	 * newTag precondition no tag created called 'mytag' postconditoin new tag
	 * created called 'mytag'
	 */
	public void create(Tag tag, Tag parent) {
		openAccordion();
		selectTag(parent);
		browser.click(browser.div("New").in(getTagSection()));
		fillInTagModalAndAssert(new CreateTagModal(browser), tag);
	}
	public void createIfNotPresent(Tag tag, Tag parent) {
		if(!isTagPresent(tag)){
			create(tag, parent);
		}
	}

	public void edit(Tag tag, Tag tag2) {
		openAccordion();
		selectTag(tag);
		clickEdit();

		fillInTagModalAndAssert(getEditModal(), tag2);
	}

	/**
	 * 
	 */
	public void clickEdit() {
		openAccordion();
		browser.click(browser.div("Edit").in(getTagSection()));
	}

	/**
	 * @param tag
	 */
	public void selectTag(Tag tag) {
		openAccordion();
		getRootTagElement().openAllTags();
		browser.click(browser.div(tag.getName()).in(getTagSection()));
	}

	/**
	 * root.getFirstChildWithName(TAG_NAME).getElementStub().click();
	 * 
	 * 
	 */
	private void confirmRemoval() {
		Dialog dialog = new Dialog("Remove Tag", browser);
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(
				dialog.getElementStub()), NUM_ATTEMPTS),
				"remove tag dialog did not display");
		dialog.getOkButton().getElementStub().click();
	}

	/**
	 * @return
	 */
	private ElementStub getTagSection() {
		return getAccordion().getSection("Tags").toElementStub(browser);
	}

	/**
	 * @return
	 */
	private ElementStub getHeader() {
		return getAccordion().getHeaderByName("Tags").toElementStub(browser);
	}

	
	/**
	 * @param tag
	 */
	private void fillInTagModalAndAssert(TagModal modal ,Tag tag) {
		fillModalFields(modal, tag);
		modal.getOkButton().getElementStub().click();
	
		verifyPresenceOfTag(tag);
	}

	/**
	 * @param tag
	 */
	private void verifyPresenceOfTag(Tag tag) {
		Assert.assertTrue(isTagPresent(tag), "newly created tag did not display");
	}
	


	/**
	 * @param tag
	 * @return
	 */
	public boolean isTagPresent(Tag tag) {
		openAccordion();
		getRootTagElement().openAllTags();
		return browser.div(tag.getName()).in(getTagSection())
				.isVisible();
	}


	private void openAccordion() {
		getHeader().click();
	}

	public void verifyTagNotPresent(Tag tag) {
		Assert.assertFalse(isTagPresent(tag), "tag is present when it should not");
	}

	public void fillEditModal(Tag tag){
		fillModalFields(getEditModal(), tag);
	}

	/**
	 * @return
	 */
	public EditTagModal getEditModal() {
		return new EditTagModal(browser);
	}
	/**
	 * @param modal
	 * @param tag
	 */
	private void fillModalFields(TagModal modal, Tag tag) {
		modal.fillNewTagModal(tag);
	}

	/**
	 * @return
	 */
	private LHSAccordion getAccordion() {
		LHSAccordion accordion = new LHSAccordion(browser);
		return accordion;
	}



}
