/**
 *
 */
package com.redhat.qe.storageconsole.te;

import javax.xml.bind.annotation.XmlAttribute;

/**
* @author shruti
* Nov 7, 2012
*/
public class AuthAllowValues {
	private String typeOfValue = null;
	private String value = null;

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	@XmlAttribute(name="value")
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the typeOfValue
	 */
	public String getTypeOfValue() {
		return typeOfValue;
	}

	/**
	 * @param typeOfValue the typeOfValue to set
	 */
	@XmlAttribute(name="typeOfValue")
	public void setTypeOfValue(String typeOfValue) {
		this.typeOfValue = typeOfValue;
	}
}
