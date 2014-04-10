/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.tags;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.elements.CreateTagModal;
import com.redhat.qe.storageconsole.helpers.elements.Dialog;
import com.redhat.qe.storageconsole.helpers.elements.TagModal;
import com.redhat.qe.storageconsole.helpers.pages.components.LHSAccordion;
import com.redhat.qe.storageconsole.mappper.Tag;
import com.redhat.qe.storageconsole.mappper.TagFactory;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.TaggingTasks;

/**
 * @author dustin 
 * Jul 16, 2013
 * 
 */
public class CreateAndDeleteTagTest extends SahiTestBase{
	
	private static Tag TAG = TagFactory.create("tag for tag create/delete tests");
	/**
	 * 
	 */
	private static final int NUM_ATTEMPTS = 20;
	/**
	 * 
	 */

	
	@BeforeMethod
	public void beforeMyMethods(){
		getHeader().click();
	}
	
	@AfterMethod
	public void removeTag(){
		getTaggingTask().removeTag(TAG);
	}

	/**
	 * @return
	 */
	private TaggingTasks getTaggingTask() {
		return new TaggingTasks(browser);
	}

	/**		root.getFirstChildWithName(TAG_NAME).getElementStub().click();

	 * 
	 */
	private void confirmRemoval() {
		Dialog dialog = new Dialog("Remove Tag", browser);
		Assert.assertTrue(WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(dialog.getElementStub()), NUM_ATTEMPTS), "remove tag dialog did not display");
		dialog.getOkButton().getElementStub().click();
	}
	
	/**
	 * @return
	 */
	private ElementStub getHeader() {
		return getAccordion().getHeaderByName("Tags").toElementStub(browser);
	}
	/*newTag
	 * precondition no tag created called 'mytag'
	 * postconditoin new tag created called 'mytag'
	 */
	@Tcms({"244659", "260600"})
	@Test
	public void newTag(){
			getTaggingTask().createIfNotPresent(TAG, new Tag("Root"));
	}

	@Tcms({"244660"})
	@Test
	public void cancelNewTag(){
		browser.click(browser.div("New").in(getTagSection()));
		TagModal tagModal = new CreateTagModal(browser);
		tagModal.fillNewTagModal(TAG);
		tagModal.getCancelButton().getElementStub().click();
		
		Assert.assertFalse(browser.div(TAG.getName()).in(getTagSection()).isVisible(), "newly tag was created");
	}

	/**
	/**
	 * @return
	 */
	private ElementStub getTagSection() {
		return getAccordion().getSection("Tags").toElementStub(browser);
	}

	/**
	 * @return
	 */
	private LHSAccordion getAccordion() {
		LHSAccordion accordion = new LHSAccordion(browser);
		return accordion;
	}
	


}
