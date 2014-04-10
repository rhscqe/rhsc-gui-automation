/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import java.io.IOException;

/**
 * @author dustin 
 * Jan 24, 2013
 */
public class CannotStartConnectException extends Exception {

	/**
	 * @param e
	 */
	public CannotStartConnectException(IOException e) {
		super(e);
	}

}
