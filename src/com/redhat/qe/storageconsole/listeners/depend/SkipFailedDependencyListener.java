/**
 * 
 */
package com.redhat.qe.storageconsole.listeners.depend;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.testng.IInvokedMethod;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.redhat.qe.storageconsole.listeners.RhscListener;

public class SkipFailedDependencyListener implements RhscListener {
	// ISuiteListene
	enum TestOutcome{
		PASS, FAIL, SKIPPED
	}

	private static Method _____methodHaltingSubsquentTests = null;
	
	private synchronized void haltAllSubsquenTests(Method method){
		_____methodHaltingSubsquentTests = method;
	}
	
	private synchronized Method methodHaltingAllTests(){
		return _____methodHaltingSubsquentTests;
	}
	
	private static ConcurrentHashMap<String, TestOutcome> testResults =  new ConcurrentHashMap<String, TestOutcome>();


	public void onTestFailedButWithinSuccessPercentage(ITestResult paramITestResult) {
		testResults.put(formattedMethodName(paramITestResult), TestOutcome.FAIL);

	}
	
	public void onTestSuccess(ITestResult paramITestResult) {
		testResults.put(formattedMethodName(paramITestResult), TestOutcome.PASS);
	}

	public void onTestFailure(ITestResult paramITestResult) {
		testResults.put(formattedMethodName(paramITestResult), TestOutcome.FAIL);
		haltAllSubequentTestsIfAnnotated(paramITestResult);
	}

	public void onTestSkipped(ITestResult paramITestResult) {
		testResults.put(formattedMethodName(paramITestResult), TestOutcome.SKIPPED);
		haltAllSubequentTestsIfAnnotated(paramITestResult);
		
	}

	private void haltAllSubequentTestsIfAnnotated(ITestResult paramITestResult) {
		if(!(isTestRunHalted()) && getTestMethod(paramITestResult).isAnnotationPresent(HaltAllSubsequentOnFailure.class)) 
			haltAllSubsquenTests(getTestMethod(paramITestResult));
	}

	/**
	 * @param paramITestResult
	 * @return
	 */
	private DependsOn getDependencyAnnotation(ITestResult paramITestResult) {
		return getTestMethod(paramITestResult).getAnnotation(DependsOn.class);
	}
	
	
	private String formattedMethodName(Method method) {
		return String.format("%s:%s", method.getDeclaringClass().getCanonicalName(), method.getName());
	}
	
	private String formattedMethodName(ITestResult paramITestResult) {
		Method method = getTestMethod(paramITestResult);
		return formattedMethodName(method);
	}

	/**
	 * @param paramITestResult
	 * @return
	 */
	private Method getTestMethod(ITestResult paramITestResult) {
		return paramITestResult.getMethod().getMethod();
	}

	public void onTestStart(ITestResult paramITestResult) {
		skipWhenDependencyFailsOrOnTestSuiteOnHalt(paramITestResult);
	}

	/**
	 * @param paramITestResult
	 */
	private void skipWhenDependencyFailsOrOnTestSuiteOnHalt(
			ITestResult paramITestResult) {
		Method method = getTestMethod(paramITestResult);
		DependsOn dependencies = method.getAnnotation(DependsOn.class);

		Collection<String> dependenciesThatDidNotRun = dependenciesThatDidNotRun(dependencies);
		Collection<String> dependenciesThatFailed = dependenciesThatFailed(dependencies);
		if(isTestRunHalted()){
			throw new SkipException(String.format("test was skipped because %s failed and is halting all subequent tests" , formattedMethodName(methodHaltingAllTests())));
		}
		else if(dependenciesThatDidNotRun(dependencies).size()> 0 )			
			throw new SkipException("test was skipped because more than one of it's dependencies did not run: " + Joiner.on(", ").join(dependenciesThatDidNotRun));
		else if(dependenciesThatFailed.size() > 0){
			throw new SkipException("test was skipped because more than one of it's dependencies failed: " + Joiner.on(", ").join(dependenciesThatFailed));
		}
	}

	/**
	 * @return
	 */
	private boolean isTestRunHalted() {
		return methodHaltingAllTests() != null;
	}

	
	private Collection<String> dependenciesThatDidNotRun(DependsOn dependencies){
		return (dependencies == null) ? new ArrayList<String>() : 
				Collections2.filter(Arrays.asList(dependencies.value()), new Predicate<String>() {
					public boolean apply(String dependency) {
						return !(testResults.keySet().contains(dependency));
					}
				});
		
	}
	
	private Collection<String> dependenciesThatFailed(final DependsOn dependencies) {
		return (dependencies == null) ? new ArrayList<String>() : 
				Collections2.filter(Arrays.asList(dependencies.value()), new Predicate<String>() {
					public boolean apply(String dependency) {
						TestOutcome depenencyOutcome = testResults.get(dependency);
						return testResults.keySet().contains(dependency) && !(depenencyOutcome.equals(TestOutcome.PASS));
					}
				});
	}




	public void onConfigurationFailure(ITestResult paramITestResult) {
		// TODO Auto-generated method stub

	}

	public void onConfigurationSkip(ITestResult paramITestResult) {
		// TODO Auto-generated method stub

	}

	public void onConfigurationSuccess(ITestResult paramITestResult) {
		// TODO Auto-generated method stub

	}
	public void onFinish(ITestContext paramITestContext) {
		// TODO Auto-generated method stub
		
	}
	
	public void onStart(ITestContext paramITestContext) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.testng.IInvokedMethodListener#afterInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
	 */
	@Override
	public void afterInvocation(IInvokedMethod paramIInvokedMethod,
			ITestResult paramITestResult) {
		
		
	}

	/* (non-Javadoc)
	 * @see org.testng.IInvokedMethodListener#beforeInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
	 */
	@Override
	public void beforeInvocation(IInvokedMethod paramIInvokedMethod,
			ITestResult paramITestResult) {
		String name = getTestMethod(paramITestResult).getName();
		skipWhenDependencyFailsOrOnTestSuiteOnHalt(paramITestResult);
		
	}

	/* (non-Javadoc)
	 * @see org.testng.ISuiteListener#onFinish(org.testng.ISuite)
	 */
	@Override
	public void onFinish(ISuite arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.testng.ISuiteListener#onStart(org.testng.ISuite)
	 */
	@Override
	public void onStart(ISuite arg0) {
		// TODO Auto-generated method stub
		
	}
}
