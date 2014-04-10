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
public class CreateNestedTagTest extends SahiTestBase{
	
	static Tag TAG = TagFactory.create("nested test: parent tag");
	static Tag NESTED_TAG = TagFactory.create("nested test: nested tag");
	static Tag NESTED_NESTED_TAG =TagFactory.create("nested test: nested nested tag"); 

	
	
	@AfterMethod
	public void removeTag(){
		getTaggingTask().removeTag(NESTED_NESTED_TAG);
		getTaggingTask().removeTag(NESTED_TAG);
		getTaggingTask().removeTag(TAG);
	}

	/**
	 * @return
	 */
	private TaggingTasks getTaggingTask() {
		return new TaggingTasks(browser);
	}

	
	/*newTag
	 * precondition no tag created called 'mytag'
	 * postconditoin new tag created called 'mytag'
	 */
	@Tcms({"260599", "260591"})
	@Test
	public void test(){
			getTaggingTask().createIfNotPresent(TAG, new Tag("Root"));
			getTaggingTask().createIfNotPresent(NESTED_TAG, TAG);
			getTaggingTask().createIfNotPresent(NESTED_NESTED_TAG, NESTED_TAG);
	}




}
