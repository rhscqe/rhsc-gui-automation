package com.redhat.qe.storageconsole.sahi.tasks;

import java.util.logging.Level;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.storageconsole.helpers.WaitUtil;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;
import com.redhat.qe.storageconsole.te.RhscCredential;

public class StorageSahiLoginLogoutTasks {
	StorageBrowser browser = null;
	
	public StorageSahiLoginLogoutTasks(StorageBrowser tasks) {
		browser = tasks;
	}
	
	//-------------------------------------------------------------------------------------
	// Login and Log out Section
	//-------------------------------------------------------------------------------------

	/*
	 * Doing Login action, if already logged-in, Will do logout and re-login with the specified authentication details
	 */
	public boolean loginOrRelogin(String userName, String password, String loginDomain) {
		
		logout(); // Do logout if already logged-in 
		if(!browser.waitForElementExists(browser, browser.textbox("LoginPopupView_userName"), "LoginPopupView_userName", 1000*180)){
			return false;
		}
		login(userName, password, loginDomain);
		return true;
	}
	
	public boolean loginOrRelogin(RhscCredential credentials) {
		return loginOrRelogin(credentials.getUsername(),credentials.getPassword(),credentials.getDomain());
	}
	
	
	

	/**
	 * @param userName
	 * @param password
	 * @param loginDomain
	 */
	private void login(String userName, String password, String loginDomain) {
		getUserNameField().setValue(userName);
		getPasswordField().setValue(password);
		getDomainField().choose(loginDomain);
		getLoginButton().click();
	}
	private void login(RhscCredential creds) {
		login(creds.getUsername(), creds.getPassword(),creds.getDomain());
	}

	/**
	 * @return
	 */
	private ElementStub getLoginButton() {
		return browser.div("LoginPopupView_loginButton");
	}

	/**
	 * @return
	 */
	private ElementStub getDomainField() {
		return browser.select("LoginPopupView_domain");
	}

	/**
	 * @return
	 */
	private ElementStub getPasswordField() {
		return browser.password("LoginPopupView_password");
	}

	/**
	 * @return
	 */
	private ElementStub getUserNameField() {
		return browser.textbox("LoginPopupView_userName");
	}
	
	public boolean loginIfLoggedOut(String userName, String password, String loginDomain){
		if(! new JQuery("a:contains('Sign Out')").addCall("size").fetch(browser).equals("0") ){
			return true;
		}else{
			WaitUtil.waitUntil(new WaitUtil.ElementIsVisible(getUserNameField()), 10, "username field is visible");
			if(!browser.isDisabled(getUserNameField())){					
				login(userName, password, loginDomain);
				return true;
			}
		}
		return false;
	}


	/*
	 * Logout action
	 */
	public boolean logout() {
		if(browser.waitForElementExists(browser, browser.link("Sign Out"), "Link: Sign Out", 1000*3)){
			browser.link("Sign Out").click();
			return true;
		} 
		return false;		
	}

	/*
	 * Re-login action. Does logout and login with specified authentication details.
	 */
	public boolean relogin(String userName, String password, String loginDomain) {
		if(!logout()){
			browser._logger.log(Level.WARNING, "Failed to logout!");
			return false;
		}		
		if(!loginOrRelogin(userName, password, loginDomain)){
			browser._logger.log(Level.WARNING, "Failed to login!");
			return false;
		}
		return true;
	}

	/**
	 * @param rhscAdminCredentials
	 */
	public void loginIfLoggedOut(RhscCredential creds) {
		loginIfLoggedOut(creds.getUsername(), creds.getPassword(),creds.getDomain());
	}
}
