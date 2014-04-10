/**
 * 
 */
package com.redhat.qe.storageconsole.mappper;

import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * @author dustin 
 * Aug 26, 2013
 */
public class TagFactory {
	public final static Random rand = new Random(200000L);
	
	public final static Tag create(String description){
		return new Tag(generateTagName(), description);
	}
	
	private static String generateTagName() {
		return String.format("%s%s", timestamp(), Math.abs(rand.nextInt()));
	}
	
	private static String timestamp() {
		DateTime time = new DateTime();
		return time.toString(DateTimeFormat.forPattern("YYYYMMddHHmmssSSS"));
	}
}
