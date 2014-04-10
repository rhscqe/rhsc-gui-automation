/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements.volume;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.testng.Assert;

import net.sf.sahi.client.Browser;

import com.google.common.base.Joiner;
import com.redhat.qe.helpers.utils.FileSize2;
import com.redhat.qe.model.WaitUtil;
import com.redhat.qe.repository.glustercli.RebalanceStatus;
import com.redhat.qe.storageconsole.helpers.elements.Row;
import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JsString;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

import dstywho.functional.Predicate;
import dstywho.regexp.RegexMatch;
import dstywho.timeout.Timeout;

/**
 * @author dustin 
 * Mar 21, 2014
 */
public class RebalanceStatusDialogTable extends TableElement{
	//COLUMN NAMES
	private static final String SIZE = "Size";
	private static final String STATUS = "Status";
	private static final String RUN_TIME = "Run Time";
	private static final String FILES_SKIPPED = "Files Skipped";
	private static final String FILES_FAILED = "Files Failed";
	private static final String FILES_SCANNED = "Files Scanned";
	private static final String FILES_REBALANCED = "Files Rebalanced";
	private static final String HOST = "Host";

	private static final int NUM_ATTEMPTS_WAIT_FOR_REBALANCE_FINISH = 140;

	public RebalanceStatusDialogTable(JQuery jqueryObj, Browser browser) {
		super(jqueryObj, browser);
	}
	
	public int getRowCount() {
		String count = getJqueryObject().addCall("find", new JsString("> tbody:visible > tr"))
				.property("length").fetch(getBrowser());
		return Integer.parseInt(count);
	}
	
	
	public void waitForRebalanceToFinish(){
		final int statusIndex = getHeaders().indexOf(STATUS);
		final int hostIndex = getHeaders().indexOf(HOST);
		for(final Row host: getRows()){
			Timeout.TIMEOUT_FIVE_SECONDS.sleep();
			Assert.assertTrue(WaitUtil.waitUntil(new Predicate() {
				
				@Override
				public Boolean act() {
					return host.getCell(statusIndex).getText().toLowerCase().contains("finished");
				}
			}, NUM_ATTEMPTS_WAIT_FOR_REBALANCE_FINISH).isSuccessful(), "rebalance finished for " +  Joiner.on(":").join(host.getCell(hostIndex).getText(),host.getCell(statusIndex).getText()));
		}
	}
	
	public ArrayList<RebalanceStatus> getStatus(){
		ArrayList<RebalanceStatus> results = new ArrayList<RebalanceStatus>();
		for(HashMap<String, String> row : getData()){
			RebalanceStatus result = new RebalanceStatus();
			result.setNodeName(getServerByName(row.get(HOST)).getHostname());
			result.setFiles(Integer.parseInt(row.get(FILES_REBALANCED))); 
			result.setLookups(Integer.parseInt(new RegexMatch(row.get(FILES_SCANNED)).find("\\d+").getText()));
			result.setFailures(Integer.parseInt(new RegexMatch(row.get(FILES_FAILED)).find("\\d+").getText()));
			result.setSkipped(Integer.parseInt(new RegexMatch(row.get(FILES_SKIPPED)).find("\\d+").getText()));
			result.setRuntime(getRunTime(row.get(RUN_TIME)));
			result.setStatusDecode(getStatusDecode(row.get(STATUS)));
			result.setSize(getSize(row.get(SIZE)));
			
			results.add(result);
		}
		return results;
	}
	
	private FileSize2 getSize(String sizeHumanReadable){
		double value = Double.parseDouble(new RegexMatch(sizeHumanReadable).find("\\d+(\\.\\d+)*").getText());
		String units = new RegexMatch(sizeHumanReadable).find("(?i)(byte|[mkg]b)").getText();
		if(units.equalsIgnoreCase("kb")){
			return FileSize2.kilobytes(BigDecimal.valueOf(value));
		}else if(units.equalsIgnoreCase("mb")){
			return FileSize2.megabytes(BigDecimal.valueOf(value));
		}else if(units.equalsIgnoreCase("gb")){
			return FileSize2.gigabytes(BigDecimal.valueOf(value));
		}else if(units.equalsIgnoreCase("bytes")){
			return FileSize2.bytes(BigDecimal.valueOf(value));
		}else{
			return FileSize2.bytes(BigDecimal.valueOf(0L));
		}
	}
	
	private Duration getRunTime(String runtime){
		runtime = runtime.trim();
		PeriodFormatter formatter = new PeriodFormatterBuilder()
	    .appendDays().appendSuffix(" d ")
	    .appendHours().appendSuffix(" h ")
	    .appendMinutes().appendSuffix(" m ")
	    .appendSeconds().appendSuffix(" s")
	    .toFormatter();
		return formatter.parsePeriod(runtime).toStandardDuration();
	}
	
	public String getStatusDecode(String rhscStatus){
		if(rhscStatus.toLowerCase().equals("finished")){
			return "completed";
		}else{throw new RuntimeException("coult not convert rhsc rebalance status to gluster status");
		}		
	}
	
	public Server getServerByName(String nodeName) {
		try{
			return TestEnvironmentConfig.getTestEnvironment().getServer(nodeName);
		}catch(TestEnvironmentConfigException e){
			throw new RuntimeException("could not get Server by name",e);
		}
	}
	

}
