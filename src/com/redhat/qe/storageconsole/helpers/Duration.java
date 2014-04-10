/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import java.util.concurrent.TimeUnit;

public class Duration {
	
	public static Duration ONE_SECOND = new Duration(TimeUnit.MILLISECONDS, 1000L);
	public static Duration TEN_SECONDS = new Duration(TimeUnit.MILLISECONDS, 10000L);
	public static Duration TWENTY_SECONDS = new Duration(TimeUnit.MILLISECONDS, 20000L);
	public static Duration  SIXTY_SECONDS = new Duration(TimeUnit.MILLISECONDS, 60000L);
	
	private TimeUnit units;
	private long interval;
	
	public Duration(TimeUnit units, long interval) {
		this.units = units;
		this.interval = interval;
	}

	public long getInterval() {
		return this.interval;
	}
	
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	public long toMilliseconds(){
		return TimeUnit.MILLISECONDS.convert(interval, units);
	}

	/**
	 * 
	 */
	public void sleep() {
		try {
			Thread.sleep(toMilliseconds());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
	}

	/**
	 * @return
	 */
	public TimeUnit getUnits() {
		return units;
	}

}
