/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests.tags;

import static com.redhat.qe.storageconsole.sahi.tests.tags.CreateNestedTagTest.NESTED_NESTED_TAG;
import static com.redhat.qe.storageconsole.sahi.tests.tags.CreateNestedTagTest.NESTED_TAG;
import static com.redhat.qe.storageconsole.sahi.tests.tags.CreateNestedTagTest.TAG;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.annoations.Tcms;
import com.redhat.qe.storageconsole.mappper.Tag;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.TaggingTasks;

/**
 * @author dustin Jul 16, 2013
 * 
 */
public class DeleteParentOfNestedTagTest extends SahiTestBase {
	
	private static final Tag ROOT = new Tag("Root");

	@BeforeMethod
	public void createTags() {
		getTaggingTask().createIfNotPresent(TAG, ROOT);
		getTaggingTask().createIfNotPresent(NESTED_TAG, TAG);
		getTaggingTask().createIfNotPresent(NESTED_NESTED_TAG, NESTED_TAG);
	}

	/**
	 * @return
	 */
	private TaggingTasks getTaggingTask() {
		return new TaggingTasks(browser);
	}

	/*
	 * newTag precondition no tag created called 'mytag' postconditoin new tag
	 * created called 'mytag'
	 */
	@Tcms({ "TBD" })
	@Test
	public void test() {
		getTaggingTask().removeTag(TAG);

		Assert.assertFalse(getTaggingTask().isTagPresent(NESTED_NESTED_TAG),
				"tag was present when should be have been removed");
		Assert.assertFalse(getTaggingTask().isTagPresent(NESTED_TAG),
				"tag was present when should be have been removed");
		Assert.assertFalse(getTaggingTask().isTagPresent(TAG),
				"tag was present when should be have been removed");
	}

}
