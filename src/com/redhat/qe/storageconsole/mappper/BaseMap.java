/**
 * 
 */
package com.redhat.qe.storageconsole.mappper;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 15, 2012
 */
public class BaseMap {
	private String resourceLocation=null;
	private boolean positive = true;
	private String errorMsg = null;


	/**
	 * @return the resourceLocation
	 */
	public String getResourceLocation() {
		return this.resourceLocation;
	}

	/**
	 * @param resourceLocation the resourceLocation to set
	 */
	public void setResourceLocation(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	/**
	 * @return the positive
	 */
	public boolean isPositive() {
		return positive;
	}

	/**
	 * @param positive the positive to set
	 */
	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}