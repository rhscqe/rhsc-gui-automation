/**
 *
 */
package com.redhat.qe.storageconsole.mappper;

/**
 * @author shruti
 * Jan 23, 2013
 */
public class VolumeOptionsMap {
	private String optionName = null;
	private String optionValue = null;
	private String editOptionValue = null;
	/**
	 * Constructor
	 */
	public VolumeOptionsMap(String optionName, String optionValue, String editOptionValue) {
		this.optionName = optionName;
		this.optionValue = optionValue;
		this.editOptionValue = editOptionValue;
	}
	/**
	 * @return the optionName
	 */
	public String getOptionName() {
		return optionName;
	}
	/**
	 * @param optionName the optionName to set
	 */
	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}
	/**
	 * @return the optionValue
	 */
	public String getOptionValue() {
		return optionValue;
	}
	/**
	 * @param optionValue the optionValue to set
	 */
	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}
	/**
	 * @return the editOptionValue
	 */
	public String getEditOptionValue() {
		return editOptionValue;
	}
	/**
	 * @param editOptionValue the editOptionValue to set
	 */
	public void setEditOptionValue(String editOptionValue) {
		this.editOptionValue = editOptionValue;
	}
}
