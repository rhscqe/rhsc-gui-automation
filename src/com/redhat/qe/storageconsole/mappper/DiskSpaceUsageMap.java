/**
 * 
 */
package com.redhat.qe.storageconsole.mappper;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Sep 13, 2012
 */
public class DiskSpaceUsageMap {
	private String fileSystem = null;
	private String type = null;
	private long sizeTotal = 0;
	private long sizeUsed = 0;
	private long sizeAvailable = 0;
	private int sizeUsedInPercentage = 0;
	private String mountedOn = null;
	/**
	 * @return the fileSystem
	 */
	public String getFileSystem() {
		return this.fileSystem;
	}
	/**
	 * @param fileSystem the fileSystem to set
	 */
	public void setFileSystem(String fileSystem) {
		this.fileSystem = fileSystem;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the sizeTotal
	 */
	public long getSizeTotal() {
		return this.sizeTotal;
	}
	/**
	 * @param sizeTotal the sizeTotal to set
	 */
	public void setSizeTotal(long sizeTotal) {
		this.sizeTotal = sizeTotal;
	}
	/**
	 * @return the sizeUsed
	 */
	public long getSizeUsed() {
		return this.sizeUsed;
	}
	/**
	 * @param sizeUsed the sizeUsed to set
	 */
	public void setSizeUsed(long sizeUsed) {
		this.sizeUsed = sizeUsed;
	}
	/**
	 * @return the sizeAvailable
	 */
	public long getSizeAvailable() {
		return this.sizeAvailable;
	}
	/**
	 * @param sizeAvailable the sizeAvailable to set
	 */
	public void setSizeAvailable(long sizeAvailable) {
		this.sizeAvailable = sizeAvailable;
	}
	/**
	 * @return the sizeUsedInPercentage
	 */
	public int getSizeUsedInPercentage() {
		return this.sizeUsedInPercentage;
	}
	/**
	 * @param sizeUsedInPercentage the sizeUsedInPercentage to set
	 */
	public void setSizeUsedInPercentage(int sizeUsedInPercentage) {
		this.sizeUsedInPercentage = sizeUsedInPercentage;
	}
	/**
	 * @return the mountedOn
	 */
	public String getMountedOn() {
		return this.mountedOn;
	}
	/**
	 * @param mountedOn the mountedOn to set
	 */
	public void setMountedOn(String mountedOn) {
		this.mountedOn = mountedOn;
	}
	
	public String toString(){
		return "FileSystem: "+this.fileSystem+", Type: "+this.type+", SizeTotal: "+this.sizeTotal+", SizeUsed: "+this.sizeUsed+", SizeAvailable: "+this.sizeAvailable+", SizeUsed(%): "+this.sizeUsedInPercentage+", MountedOn: "+this.mountedOn;
	}
	
}
