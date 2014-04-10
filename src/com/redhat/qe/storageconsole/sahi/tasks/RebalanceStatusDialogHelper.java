/**
 * 
 */
package com.redhat.qe.storageconsole.sahi.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;

import com.google.common.base.Joiner;
import com.redhat.qe.helpers.utils.CollectionUtils;
import com.redhat.qe.model.Host;
import com.redhat.qe.model.WaitUtil;
import com.redhat.qe.repository.glustercli.RebalanceStatus;
import com.redhat.qe.storageconsole.helpers.Times;
import com.redhat.qe.storageconsole.helpers.elements.Row;
import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.elements.volume.RebalanceStatusDialog;
import com.redhat.qe.storageconsole.helpers.elements.volume.RebalanceStatusDialogTable;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

import dstywho.functional.Predicate;
import dstywho.regexp.RegexMatch;
import dstywho.timeout.Timeout;

/**
 * @author dustin 
 * Mar 25, 2014
 */
public class RebalanceStatusDialogHelper {
	
	private RebalanceStatusDialog dialog;

	public RebalanceStatusDialogHelper(RebalanceStatusDialog dialog){
		this.dialog = dialog;
	}
	
	public void waitForRebalanceToFinish(){
		dialog.getTable().waitForRebalanceToFinish();
	}
	
	
	


}
