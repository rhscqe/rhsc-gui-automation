/**
 * 
 */
package com.redhat.qe.storageconsole.te;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.redhat.qe.factories.HostFactory;
import com.redhat.qe.model.Cluster;
import com.redhat.qe.model.Host;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 2, 2012
 */
public interface IServer extends Sshable{
	/**
	 * @return the name
	 */
	public String getName();

}
