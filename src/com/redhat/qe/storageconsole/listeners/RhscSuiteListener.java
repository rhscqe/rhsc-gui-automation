/**
 * 
 */
package com.redhat.qe.storageconsole.listeners;

import java.util.ArrayList;
import java.util.List;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

import com.redhat.qe.model.WaitUtil.Nothing;
import com.redhat.qe.storageconsole.helpers.Closure;
import com.redhat.qe.storageconsole.listeners.depend.SkipFailedDependencyListener;

import dstywho.functional.Closure2;

/**
 * @author dustin 
 * May 21, 2013
 * 
 * testng can't order listenders. this will serve as the main listener that will notify listener in the order subscribed
 * TODO fine grained control on certain method orders for each listener by using an priority annotation
 */
public class RhscSuiteListener implements IResultListener, IInvokedMethodListener,ISuiteListener {
	
	
	public List<RhscListener> subscribers = new ArrayList<RhscListener>();
	
	public RhscSuiteListener(){
		subscribers.add(new TestNGInfoFailureScreenClearListener());
		subscribers.add(new RhscReportEngineListener());
		subscribers.add(new SkipFailedDependencyListener());
	}
	
	
	public void sendInOrder(Closure2<Integer, RhscListener> c){
		for( RhscListener subscriber : subscribers ){
			c.call(subscriber);
		}
		
	}


	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onFinish(org.testng.ITestContext)
	 */
	@Override
	public void onFinish(final ITestContext paramITestContext) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onFinish(paramITestContext);
				return null;
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onStart(org.testng.ITestContext)
	 */
	@Override
	public void onStart( final ITestContext paramITestContext) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onStart(paramITestContext);
				return null;
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestFailedButWithinSuccessPercentage(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailedButWithinSuccessPercentage(
			final ITestResult paramITestResult) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onTestFailedButWithinSuccessPercentage(paramITestResult);
				return null;
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestFailure(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailure(final ITestResult paramITestResult) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onTestFailure(paramITestResult);
				return null;
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestSkipped(org.testng.ITestResult)
	 */
	@Override
	public void onTestSkipped(final ITestResult paramITestResult) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onTestSkipped(paramITestResult);
				return null;
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestStart(org.testng.ITestResult)
	 */
	@Override
	public void onTestStart(final ITestResult paramITestResult) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onTestStart(paramITestResult);
				return null;
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onTestSuccess(final ITestResult paramITestResult) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onTestSuccess(paramITestResult);
				return null;
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.testng.IConfigurationListener#onConfigurationFailure(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationFailure(final ITestResult paramITestResult) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onConfigurationFailure(paramITestResult);
				return null;
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.testng.IConfigurationListener#onConfigurationSkip(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationSkip(final ITestResult paramITestResult) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onConfigurationSkip(paramITestResult);
				return null;
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.testng.IConfigurationListener#onConfigurationSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationSuccess(final ITestResult paramITestResult) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onConfigurationSuccess(paramITestResult);
				return null;
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.testng.IInvokedMethodListener#afterInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
	 */
	@Override
	public void afterInvocation(final IInvokedMethod paramIInvokedMethod,
			final ITestResult paramITestResult) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.afterInvocation(paramIInvokedMethod, paramITestResult);
				return null;
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.testng.IInvokedMethodListener#beforeInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
	 */
	@Override
	public void beforeInvocation(final IInvokedMethod paramIInvokedMethod,
			final ITestResult paramITestResult) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.beforeInvocation(paramIInvokedMethod, paramITestResult);
				return null;
			}
		});
		
	}


	/* (non-Javadoc)
	 * @see org.testng.ISuiteListener#onFinish(org.testng.ISuite)
	 */
	@Override
	public void onFinish(final ISuite arg0) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onFinish(arg0);
				return null;
			}
		});
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.testng.ISuiteListener#onStart(org.testng.ISuite)
	 */
	@Override
	public void onStart(final ISuite arg0) {
		sendInOrder(new Closure2<Integer, RhscListener>() {
			
			@Override
			public Integer act(RhscListener paramV) {
				paramV.onStart(arg0);
				return null;
			}
		});
		// TODO Auto-generated method stub
		
	}


	
	


}
