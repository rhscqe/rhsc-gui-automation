/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.google.common.base.Predicate;
import com.redhat.qe.storageconsole.sahi.tasks.GuiTables;

import dstywho.timeout.Timeout;

/**
 * @author dustin 
 * Apr 23, 2013
 */
public class WaitUtil {
	
	
	
	
	public static class ElementIsNotVisible implements Predicate<Integer> {
		
		private ElementStub element;
		
		public ElementIsNotVisible(ElementStub element){
			this.element = element;
		}
		
		public boolean apply(Integer att){
			return !this.element.isVisible();
		}
	};
	public static class ElementIsVisible implements Predicate<Integer> {
		
		private ElementStub element;

		public ElementIsVisible(ElementStub element){
			this.element = element;
		}
		
		public boolean apply(Integer att){
			return this.element.isVisible();
		}
	};
	
	
	private static Logger LOG = Logger.getLogger(WaitUtil.class.getName());

	public static boolean waitUntil(Predicate<Integer> condition, int numAttempts, String message){
		return(waitUntil(condition, numAttempts, Timeout.TIMEOUT_ONE_SECOND,message));
	}

	public static boolean waitUntil(Predicate<Integer> condition, int numAttempts, Timeout delay, String message){
		for(int attempt = 1 ; attempt <= numAttempts; attempt ++){
			delay.sleep();
			LOG.info(String.format("waiting until %s. attempt %s/%s", message, attempt, numAttempts));
			if( condition.apply(attempt))
				return true;
		}
		return false;
	}
	
	public static boolean waitUntil(Predicate<Integer> condition, int numAttempts ){
		return waitUntil(condition, numAttempts, "");
	}

	public static boolean waitUntil(Predicate<Integer> condition, int numAttempts , Timeout delay){
		return waitUntil(condition, numAttempts, delay,  "");
	}
	

}
