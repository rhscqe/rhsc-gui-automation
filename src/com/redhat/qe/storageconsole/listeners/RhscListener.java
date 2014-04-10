/**
 * 
 */
package com.redhat.qe.storageconsole.listeners;

import org.testng.IInvokedMethodListener;
import org.testng.ISuiteListener;
import org.testng.internal.IResultListener;

/**
 * @author dustin 
 * May 21, 2013
 * 
 * testng can't order listenders. this will serve as the main listender that will notify listenders in the order subscribed
 * 
 */
public interface RhscListener extends IResultListener, IInvokedMethodListener,ISuiteListener {
	

}
