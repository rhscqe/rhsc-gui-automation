/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 11, 2012
 */
public class Bricks {
	private List<Brick> bricks = new ArrayList<Brick>();
	private String name=null;

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	@XmlAttribute(name="name")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the bricks
	 */
	public List<Brick> getBricks() {
		return this.bricks;
	}

	/**
	 * @param bricks the bricks to set
	 */
	@XmlElement(name="brick")
	public void setBricks(List<Brick> bricks) {
		this.bricks = bricks;
	}

}
