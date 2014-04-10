/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import org.testng.annotations.Test;

/**
 * @author dustin 
 * Jul 16, 2013
 */
public class CleanerTest {
	@Test
	public void cleanup(){
		RhscCleanerTool.cleanup();
	}

}
