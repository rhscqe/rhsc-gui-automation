/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import org.testng.Assert;

/**
 * @author dustin 
 * Jan 29, 2013
 */
public class AssertUtil {
	public static void failTest(String reason){
		Assert.fail(reason);
	}
}
