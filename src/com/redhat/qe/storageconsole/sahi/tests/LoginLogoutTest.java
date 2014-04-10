/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tests;

import java.util.logging.Level;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.redhat.qe.storageconsole.helpers.elements.Cell;
import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiLoginLogoutTasks;
import com.redhat.qe.storageconsole.te.RhscCredential;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 2, 2012
 */
public class LoginLogoutTest extends SahiTestBase{
	
	StorageSahiLoginLogoutTasks tasks = null;
	
	@BeforeMethod
	public void setUp() {
		tasks = new StorageSahiLoginLogoutTasks(browser);
	}
	
	@Test
	public void loginTest(){
		_logger.log(Level.FINE, "Logging into RHSC GUI");
		RhscCredential credentials = TestEnvironmentConfig.getTestEnvironment().getRhscAdminCredentials();
		Assert.assertTrue(tasks.loginOrRelogin(credentials.getUsername(), credentials.getPassword(), credentials.getDomain()), "Login status");
		String loginErrorMessgae = "Login failed. Please verify your login information or contact the system administrator.";
		Assert.assertFalse(browser.div(loginErrorMessgae).exists(), "Login error message["+loginErrorMessgae+"] available?: "+browser.div(loginErrorMessgae).exists());
		Assert.assertFalse(browser.textbox("LoginPopupView_userName").exists(), "Login user TextBox available?: "+browser.textbox("user").exists());
		Assert.assertFalse(browser.password("LoginPopupView_password").exists(), "Login user password field available?: "+browser.password("password").exists());
		Assert.assertTrue(browser.div("/" + "Logged in user: " + credentials.getUsername() + "/").exists(), "Username not found!");
		Assert.assertTrue(browser.div("/" + "Sign Out" + "/").exists());
	}
	
	@Test
	public void logoutTest(){
		_logger.finer("Logging out from RHSC GUI");
		tasks.logout();
		Assert.assertTrue(browser.textbox("LoginPopupView_userName").exists(), "Login user TextBox available?: "+browser.textbox("LoginPopupView_userName").exists());
		Assert.assertTrue(browser.password("LoginPopupView_password").exists(), "Login user password field available?: "+browser.password("LoginPopupView_password").exists());
		Assert.assertTrue(browser.div("LoginPopupView_loginButton").exists(), "Login button available?: "+browser.cell("LoginPopupView_loginButton").exists());
	}
	
	@Test
	public void loginWithInvalidUsername(){	
		_logger.finer("Failed Login into RHSC GUI");
		Assert.assertTrue(tasks.loginOrRelogin("invalidusername", "redhat", "internal"), "Login user TextBox available?: "+browser.textbox("LoginPopupView_userName").exists());
		Assert.assertTrue(browser.textbox("LoginPopupView_userName").exists(), "Login user TextBox available?: "+browser.textbox("LoginPopupView_userName").exists());
		String loginErrorMessgae = "Login failed. Please verify your login information or contact the system administrator.";
		Assert.assertTrue(browser.div(loginErrorMessgae).exists(), "Login error message["+loginErrorMessgae+"] available?: "+browser.div(loginErrorMessgae).exists());
	}
}
