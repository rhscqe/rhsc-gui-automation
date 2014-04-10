/**
 * 
 */
package com.redhat.qe.storageconsole.mappper;

/**
 * @author dustin 
 * Aug 6, 2013
 */
public class Tag {
	
	public Tag(String name, String description) {
		this.name = name;
		this.description = description;
	}
	public Tag(String name){
		this(name,"");
	}
	private String name;
	private String description;
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
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
