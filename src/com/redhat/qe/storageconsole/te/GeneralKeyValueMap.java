/**
 *
 */
package com.redhat.qe.storageconsole.te;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author shruti
 * Nov 16, 2012
 */
public class GeneralKeyValueMap {
	private String key = null;
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
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	@XmlAttribute(name="key")
	public void setKey(String key) {
		this.key = key;
	}

}