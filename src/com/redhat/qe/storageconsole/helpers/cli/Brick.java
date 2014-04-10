/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.cli;
import com.redhat.qe.storageconsole.helpers.RegexMatch;

/**
 * @author dustin 
 * Jan 15, 2013
 */
public class Brick {
	String name;
	String hostname;
	String location;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return this.hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	public static Brick parse(String line){
		Brick brick = new Brick();
		brick.setName(new RegexMatch(line).find("Brick\\d").get(0).getText());
		//brick.setLocation(new RegexMatch(line).find(":.\\w+:").get(0).getText().replaceAll(":", ""));
		String[] splitLine = line.split(":");
		brick.setLocation(splitLine[splitLine.length-1]);
		return brick;
	}
}
