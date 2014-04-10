package com.redhat.qe.storageconsole.sahi.tasks;

import java.net.InetAddress;
import java.util.Arrays;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

import com.redhat.reportengine.client.ClientCommon;
import com.redhat.reportengine.client.RemoteAPI;

public class ReportEngineListener implements IResultListener, ISuiteListener {
	public static RemoteAPI reportEngine = new RemoteAPI();

	public ReportEngineListener() throws Exception {
		reportEngine.initClient(InetAddress.getLocalHost().getHostName() + " [" + InetAddress.getLocalHost().getHostAddress() + "]");
	}

	public String getParametersString(Object[] parameters) {
		String parametersString = "";
		if ((parameters != null) && (parameters.length > 0)) {
			parametersString = Arrays.deepToString(parameters);
		}
		return parametersString;
	}

	public void onFinish(ITestContext context) {
		reportEngine.isClientConfigurationSuccess();
	}

	public void onStart(ITestContext context) {
		if (!(reportEngine.isClientConfigurationSuccess()))
			return;
		try {
			reportEngine.insertTestGroup(context.getName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
	}

	public void onTestFailure(ITestResult result) {
		if (!(reportEngine.isClientConfigurationSuccess()))
			return;
		try {
			reportEngine.takeScreenShot();
			reportEngine.updateTestCase("Failed", ClientCommon.toString(result.getThrowable()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onTestSkipped(ITestResult result) {
		if (!(reportEngine.isClientConfigurationSuccess()))
			return;
		try {
			if (reportEngine.isLastTestStateRunning()) {
				reportEngine.updateTestCase("Skipped");
				return;
			}
			reportEngine.insertTestCase(result.getName(), result.getName() + "(" + getParametersString(result.getParameters()) + ")", "Skipped");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onTestStart(ITestResult test) {
		if (!(reportEngine.isClientConfigurationSuccess()))
			return;
		try {
			reportEngine.insertTestCase(test.getName(), test.getName() + "(" + getParametersString(test.getParameters()) + ")", "Running");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onTestSuccess(ITestResult result) {
		if (!(reportEngine.isClientConfigurationSuccess()))
			return;
		try {
			reportEngine.updateTestCase("Passed");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onConfigurationFailure(ITestResult arg0) {
		if (reportEngine.isClientConfigurationSuccess())
			reportEngine.runLogHandler();
	}

	public void onConfigurationSkip(ITestResult arg0) {
		if (reportEngine.isClientConfigurationSuccess())
			reportEngine.runLogHandler();
	}

	public void onConfigurationSuccess(ITestResult arg0) {
		if (reportEngine.isClientConfigurationSuccess())
			reportEngine.runLogHandler();
	}

	public void onFinish(ISuite suite) {
		if (!(reportEngine.isClientConfigurationSuccess()))
			return;
		try {
			reportEngine.updateTestSuite("Completed", getBuildVersion());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onStart(ISuite suite) {
		if (!(reportEngine.isClientConfigurationSuccess()))
			return;
		try {
			reportEngine.updateTestSuiteName(suite.getName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getBuildVersion() {
		return System.getProperty(reportEngine.getBuildVersionReference());
	}

}