/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import java.net.MalformedURLException;
import java.net.URL;

import com.redhat.qe.config.RestApi;
import com.redhat.qe.ssh.Credentials;
import com.redhat.qe.storageconsole.te.RhscCredential;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 * @author dustin 
 * Oct 4, 2013
 */
public class RestApiConfiguration {
	
	
	private static String getHostName(){
		String url = TestEnvironmentConfig.getTestEnvironment().getRhsGuiHttpsUrl();
		try {
			return new URL(url).getHost();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	

	public static RestApi getRestApi(){
		RhscCredential rhscCredentials = TestEnvironmentConfig.getTestEnvironment().getRhscAdminCredentials();
		return new RestApi(getHostName(), new Credentials(getLoginWithDomain(rhscCredentials), rhscCredentials.getPassword()));  
	}
	/**
	 * @param rhscCredentials
	 * @return
	 */
	private static String getLoginWithDomain(RhscCredential rhscCredentials) {
		return rhscCredentials.getUsername() + "@" + rhscCredentials.getDomain();
	}
}
