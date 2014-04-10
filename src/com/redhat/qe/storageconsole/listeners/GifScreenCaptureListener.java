/**
 * 
 */
package com.redhat.qe.storageconsole.listeners;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

import com.google.common.base.Joiner;
import com.redhat.qe.storageconsole.helpers.ReadInput;
import com.redhat.qe.storageconsole.helpers.RegexMatch;
import com.redhat.qe.storageconsole.helpers.Response;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;

/**
 */
public class GifScreenCaptureListener implements IResultListener, ISuiteListener{

	private static Logger LOG = Logger.getLogger(GifScreenCaptureListener.class.getName());
	private Process process;

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestFailure(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailure(ITestResult result) {
		stopVideoCapture();
	}

	/**
	 * 
	 */
	private void stopVideoCapture() {
		if(process != null){
			try {
				OutputStream outputStream = process.getOutputStream();
				outputStream.write("q".getBytes());
				outputStream.flush();
				process.destroy();
			} catch (IOException e) {
				e.printStackTrace();
				
			}catch (Exception e){
				e.printStackTrace();
			}
			
	
		}
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestStart(org.testng.ITestResult)
	 */
	@Override
	public void onTestStart(ITestResult result) {
		String fileName = fileNameForTest(result);
		 String display = getDisplayFromConfiguration();
		 startCapturingVideo(fileName, display);
	}

	/**
	 * @param result
	 * @return
	 */
	private String fileNameForTest(ITestResult result) {
//		String testName = (result.getName()+"_"+getParametersString(result.getParameters())).replaceAll("[^0-9a-zA-Z\\.]", "_");
		String testName = (result.getName()+"_"+formattedMethodName(result)).replaceAll("[^a-zA-Z\\d\\.]", "_");
		String fileName = String.format("/tmp/%s.gif", testName);
		return fileName;
	}
	private String formattedMethodName(Method method) {
		return String.format("%s:%s", method.getDeclaringClass().getCanonicalName(), method.getName());
	}
	
	private String formattedMethodName(ITestResult paramITestResult) {
		Method method = paramITestResult.getMethod().getMethod();
		return formattedMethodName(method);
	}

	/**
	 * @return
	 */
	public String getDisplayFromConfiguration() {
		String display;
		try {
			display = new RegexMatch(TestEnvironmentConfig.getTestEnvironemt().getBrowser().getOptions()).find(":\\d(.\\d){0,1}").get(0).getText();
		} catch (Exception e) {
			throw new RuntimeException("could not obtain display number to record video");
		}
		return display;
	}

	/**
	 * @param fileName
	 * @param display
	 */
	private void startCapturingVideo(String fileName, String display) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		String screenDimentions = String.format("%sx%s", (int)Math.floor(screenSize.getWidth()), (int)Math.floor(screenSize.getHeight()));
		ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-y", "-f", "x11grab", "-r", "1", "-s", screenDimentions, "-i", display , "-pix_fmt", "rgb24", fileName);
		LOG.info( Joiner.on(" ").join(pb.command()));
	    try {
			process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
			FutureTask<Response> task = new FutureTask<Response>(new ReadInput(process.getErrorStream()));
			task.run();
	}

	/* (non-Javadoc)t
	 * @see org.testng.ITestListener#onTestSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onTestSuccess(ITestResult result) {
		stopVideoCapture();
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestSkipped(org.testng.ITestResult)
	 */
	@Override
	public void onTestSkipped(ITestResult paramITestResult) {
		stopVideoCapture();
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestFailedButWithinSuccessPercentage(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult paramITestResult) {
		stopVideoCapture();
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onStart(org.testng.ITestContext)
	 */
	@Override
	public void onStart(ITestContext paramITestContext) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onFinish(org.testng.ITestContext)
	 */
	@Override
	public void onFinish(ITestContext paramITestContext) {
		stopVideoCapture();
	}

	/* (non-Javadoc)
	 * @see org.testng.internal.IConfigurationListener#onConfigurationFailure(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationFailure(ITestResult paramITestResult) {
		stopVideoCapture();
	}

	/* (non-Javadoc)
	 * @see org.testng.internal.IConfigurationListener#onConfigurationSkip(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationSkip(ITestResult paramITestResult) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.testng.internal.IConfigurationListener#onConfigurationSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationSuccess(ITestResult paramITestResult) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.testng.ISuiteListener#onFinish(org.testng.ISuite)
	 */
	@Override
	public void onFinish(ISuite paramISuite) {
		stopVideoCapture();
		
	}

	/* (non-Javadoc)
	 * @see org.testng.ISuiteListener#onStart(org.testng.ISuite)
	 */
	@Override
	public void onStart(ISuite paramISuite) {
		
	}
	
	public String getOutputFile(ITestResult result) {
		File f = new File(fileNameForTest(result));
		if (f.exists()) {
			return fileNameForTest(result);
		} else {
			return null;
		}
	}

	public String getParametersString(Object[] parameters) {
		String parametersString = "";
		if (parameters != null && parameters.length > 0){
			parametersString = Arrays.deepToString(parameters);
		}			
		return parametersString;
	} 
}
