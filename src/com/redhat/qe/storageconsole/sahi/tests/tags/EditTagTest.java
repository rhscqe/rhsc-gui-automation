/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.tags;

import net.sf.sahi.client.ElementStub;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.annoations.Tcms;
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
public class EditTagTest extends SahiTestBase{
	
	/**
	 * 
	 */
	private static final Tag ROOT_TAG = new Tag("Root");
	private static Tag TAG = TagFactory.create("edit test tag: initial");
	private static Tag TAG_TO_REMOVE;
	
	@BeforeMethod
	public void beforeMyMethods(){
		getHeader().click();
		getTaggingTask().createIfNotPresent(TAG,ROOT_TAG);
	}
	
	@AfterMethod
	public void removeTag(){
		getTaggingTask().removeTag(TAG);
		if(TAG_TO_REMOVE != null)
			getTaggingTask().removeTag(TAG_TO_REMOVE);
	}

	private TaggingTasks getTaggingTask() {
		return new TaggingTasks(browser);
	}

	private ElementStub getHeader() {
		return getAccordion().getHeaderByName("Tags").toElementStub(browser);
	}


	@Tcms({"244661"})
	@Test
	public void editTest(){
		Tag tagEdited = TagFactory.create("edit tag: editted");
		getTaggingTask().edit(TAG, tagEdited);
		TAG_TO_REMOVE = tagEdited;
	}
	
	@Tcms({"244662"})
	@Test
	public void editCancelTest(){
		getTaggingTask().selectTag(TAG);
		getTaggingTask().clickEdit();
		Tag tagEdittedToBe = TagFactory.create("edit tag: edit attempt");
		getTaggingTask().getEditModal().fillNewTagModal(tagEdittedToBe);
		getTaggingTask().getEditModal().getCancelButton().getElementStub().click();
		getTaggingTask().verifyTagNotPresent(tagEdittedToBe);
	}

	/**
	 * @return
	 */
	private LHSAccordion getAccordion() {
		LHSAccordion accordion = new LHSAccordion(browser);
		return accordion;
	}
	


}
