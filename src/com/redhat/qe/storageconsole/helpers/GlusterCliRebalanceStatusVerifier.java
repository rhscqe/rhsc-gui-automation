package com.redhat.qe.storageconsole.helpers;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;

import com.redhat.qe.model.Host;
import com.redhat.qe.repository.glustercli.RebalanceStatus;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

public class GlusterCliRebalanceStatusVerifier{
		Host hostWhereCommandWasRun;
		List<RebalanceStatus> glusterStatuses;
		private List<RebalanceStatus> rhscDialogStatuses;

		
		/**
		 * @param hostWhereCommandWasRun
		 * @param glusterStatuses
		 */
		public GlusterCliRebalanceStatusVerifier(Host hostWhereCommandWasRun, List<RebalanceStatus> glusterStatuses, List<RebalanceStatus> rhscdialogStatuses) {
			super();
			this.hostWhereCommandWasRun = hostWhereCommandWasRun;
			this.glusterStatuses = glusterStatuses;
			this.rhscDialogStatuses = rhscdialogStatuses;
		}

		
		public void verify(){
			List<RebalanceStatus> glusterStats = adjustGlusterNodeNameForLocalHost();
			Assert.assertEquals(rhscDialogStatuses.size(), glusterStats.size());
			for(RebalanceStatus actual : rhscDialogStatuses){
				RebalanceStatus expected = getStatusByIpOrHostname( actual.getNodeName(), glusterStats);
				expected._verifyEquals(actual);
			}
		}
		
		private List<RebalanceStatus> adjustGlusterNodeNameForLocalHost(){
			ArrayList<RebalanceStatus> results = new ArrayList<RebalanceStatus>();
			for(RebalanceStatus status : glusterStatuses){
				if(status.getNodeName().equals("localhost"))
					status.setNodeName(hostWhereCommandWasRun.getAddress());
				results.add(status);

			}
			return results;
			
		}

		private RebalanceStatus getStatusByIpOrHostname(final String hostOrIp, List<RebalanceStatus> statuses ){
			return CollectionUtils.findFirst(statuses, new com.google.common.base.Predicate<RebalanceStatus>() {

				@Override
				public boolean apply(RebalanceStatus input) {
					return hostOrIp.equals(input.getNodeName());
				}
			});
		}
		

	}