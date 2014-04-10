/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 10, 2012
 */
public class VersionTest extends SahiTestBase{

	@Test
	public void getVersion(){
		String versionInfo = browser.getversion();
		Assert.assertTrue(versionInfo.length()>0, "Validate version info");
		System.setProperty("rhsc.build.version", versionInfo);
		Reporter.log("<BR>Red Hat Storage Console Version: <b>"+versionInfo+"</b>");
	}
}
