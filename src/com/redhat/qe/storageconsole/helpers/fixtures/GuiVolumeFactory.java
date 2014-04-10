/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import com.redhat.qe.factories.BrickFactory;
import com.redhat.qe.factories.VolumeFactory;
import com.redhat.qe.helpers.utils.AbsolutePath;

/**
 * @author dustin 
 * Mar 24, 2014
 */
public class GuiVolumeFactory extends VolumeFactory{
	
	public GuiVolumeFactory(){
		super(new BrickFactory(AbsolutePath.from("/bricks")));
	}
	

}
