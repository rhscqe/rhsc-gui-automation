/**
 * 
 */
package com.redhat.qe.storageconsole.listeners;

import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.redhat.reportengine.client.ReportEngineClientTestNGListener;

/**
 * @author dustin 
 * Jul 19, 2013
 */
public class RhscReportEngineListener extends ReportEngineClientTestNGListener implements RhscListener{

	/* (non-Javadoc)
	 * @see org.testng.IInvokedMethodListener#beforeInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
	 */
	@Override
	public void beforeInvocation(IInvokedMethod paramIInvokedMethod,
			ITestResult paramITestResult) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.testng.IInvokedMethodListener#afterInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
	 */
	@Override
	public void afterInvocation(IInvokedMethod paramIInvokedMethod,
			ITestResult paramITestResult) {
		// TODO Auto-generated method stub
		
	}

}