/**
 * 
 */
package com.redhat.qe.storageconsole.listeners;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.IInvokedMethod;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

import com.redhat.qe.storageconsole.sahi.base.SahiTestBase;
import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;
import com.redhat.qe.storageconsole.te.StorageConsoleConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 5, 2012
 */
public class TestNGInfoFailureScreenClearListener implements RhscListener{
	private static Logger _logger = Logger.getLogger(TestNGInfoFailureScreenClearListener.class.getName());
	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onFinish(org.testng.ITestContext)
	 */
	@Override
	public void onFinish(ITestContext test) {
		_logger.log(Level.INFO, "Finished TestNG Test Script: "+test.getName());		
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onStart(org.testng.ITestContext)
	 */
	@Override
	public void onStart(ITestContext test) {
		_logger.log(Level.INFO, "");			
		_logger.log(Level.INFO, "");			
		_logger.log(Level.INFO, "");			
		_logger.log(Level.INFO, "");			
		_logger.log(Level.INFO, "Starting TestNG Test Script: "+test.getName());			
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestFailedButWithinSuccessPercentage(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		_logger.log(Level.WARNING, "Test Failed but within Success Percentage: "+result.getName(), result.getThrowable());			
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestFailure(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailure(ITestResult result) {
		_logger.log(Level.SEVERE, "Test Failed: "+result.getName()+"("+getParametersString(result.getParameters())+")", result.getThrowable());	
		if(! TestEnvironmentConfig.getTestEnvironment().getBrowser().isReopenForEachTest())
			clearPopUpsOnCurrentScreen();
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestSkipped(org.testng.ITestResult)
	 */
	@Override
	public void onTestSkipped(ITestResult result) {
		_logger.log(Level.WARNING, "Test Skipped: "+result.getName()+"("+getParametersString(result.getParameters())+")", result.getThrowable());				
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestStart(org.testng.ITestResult)
	 */
	@Override
	public void onTestStart(ITestResult result) {
		_logger.log(Level.INFO, "*** Starting Test: "+result.getName()+"("+getParametersString(result.getParameters())+")", result.getThrowable());				
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onTestSuccess(ITestResult result) {
		_logger.log(Level.INFO, "*** Test Completed successfully: "+result.getName());		
	}

	/* (non-Javadoc)
	 * @see org.testng.internal.IConfigurationListener#onConfigurationFailure(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationFailure(ITestResult result) {
		_logger.log(Level.SEVERE, "Failed to load TestNG Confguration!!", result.getThrowable());		
	}

	/* (non-Javadoc)
	 * @see org.testng.internal.IConfigurationListener#onConfigurationSkip(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationSkip(ITestResult result) {
		_logger.log(Level.WARNING, "Skipped TestNG Confguration!!", result.getThrowable());				
	}

	/* (non-Javadoc)
	 * @see org.testng.internal.IConfigurationListener#onConfigurationSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationSuccess(ITestResult result) {
		_logger.log(Level.INFO, "Loaded TestNG Confguration Success!!");
		
	}

	/* (non-Javadoc)
	 * @see org.testng.ISuiteListener#onFinish(org.testng.ISuite)
	 */
	@Override
	public void onFinish(ISuite suite) {
		_logger.log(Level.INFO, "Finishing TestNG Suite: "+suite.getName());		
	}

	/* (non-Javadoc)
	 * @see org.testng.ISuiteListener#onStart(org.testng.ISuite)
	 */
	@Override
	public void onStart(ISuite suite) {
		_logger.log(Level.INFO, "Starting TestNG Suite: "+suite.getName());		
	}
	
	public String getParametersString(Object[] parameters) {
		String parametersString = "";
		if (parameters != null && parameters.length > 0){
			parametersString = Arrays.deepToString(parameters);
		}			
		return parametersString;
	} 
	
	
	public void clearPopUpsOnCurrentScreen(){
		StorageBrowser storageSahiTasks = SahiTestBase.getStorageSahiTasks();
		String closeButton = "Close";
		String cancelButton = "/_Cancel/";
		
		//Check do we have Close button on screen
		if(storageSahiTasks.exists(storageSahiTasks.div(closeButton))){
			_logger.log(Level.INFO, "Button ["+closeButton+"] is available to click");
			//TODO: unable to close the About Pop-up with single click. It Needs 3 clicks. This is a temporary workaround. Should be fixed on the right way 
			for(int i=0;i<3;i++){
				if(storageSahiTasks.div(closeButton).exists()){
					storageSahiTasks.div(closeButton).click();
				}else{
					break;
				}				
			}
		}		
		
		int cancelCount = storageSahiTasks.div(cancelButton).countSimilar();
		_logger.log(Level.INFO, "Button Count["+cancelButton+"]: "+cancelCount);
		for(int i=0; i<cancelCount;i++){
			storageSahiTasks.div(cancelButton+"["+i+"]").click();
			_logger.log(Level.INFO, "Clicked on the button "+cancelButton+"["+i+"]");
		}
		
		if (storageSahiTasks.div("Cancel").exists()) {
			_logger.log(Level.INFO, "Closing popup");
			storageSahiTasks.closePopup("Cancel");
		}
		
	}

	/* (non-Javadoc)
	 * @see org.testng.IInvokedMethodListener#afterInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
	 */
	@Override
	public void afterInvocation(IInvokedMethod arg0, ITestResult arg1) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.testng.IInvokedMethodListener#beforeInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
	 */
	@Override
	public void beforeInvocation(IInvokedMethod arg0, ITestResult arg1) {
		// TODO Auto-generated method stub
		
	}

}
