/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.fixtures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.redhat.qe.factories.BrickFactory;
import com.redhat.qe.helpers.MountedVolume;
import com.redhat.qe.helpers.rebalance.SimpleVolumePopulationStrategy;
import com.redhat.qe.helpers.rebalance.VolumePopulationStrategy;
import com.redhat.qe.helpers.repository.JobRepoHelper;
import com.redhat.qe.helpers.repository.RebalanceStatusWaiter;
import com.redhat.qe.model.Job;
import com.redhat.qe.repository.JobRepository;
import com.redhat.qe.repository.rest.BrickRepository;

/**
 * @author dustin 
 * Mar 19, 2014
 */
public class RebalanceTestBase extends GuiPopulatedVolumeTestBase{
	
	@BeforeMethod
	public void addBricksToVolume(){
		new BrickRepository(getSession(), getCreateCluster(), getCreatedVolume()).create(new BrickFactory().brick(getCreatedHosts().get(0)));
		new BrickRepository(getSession(), getCreateCluster(), getCreatedVolume()).create(new BrickFactory().brick(getCreatedHosts().get(0)));
		new BrickRepository(getSession(), getCreateCluster(), getCreatedVolume()).create(new BrickFactory().brick(getCreatedHosts().get(0)));
		new BrickRepository(getSession(), getCreateCluster(), getCreatedVolume()).create(new BrickFactory().brick(getCreatedHosts().get(0)));
	}

	@Override
	protected VolumePopulationStrategy getVolumePopulationStrategy( MountedVolume mountedVolume) {
		return new SimpleVolumePopulationStrategy(mountedVolume);
	}
	
	
	@AfterMethod(alwaysRun=true)
	public void waitForRebalanceJobsTofinish(){
		Collection<Job> jobs = getStartedRebalanceJobs();
		for(Job job: jobs){
			Assert.assertTrue(new RebalanceStatusWaiter(getJobRepository()).waitForRebalanceStepToFinish(job).isSuccessful(), "timed out waiting for rebalance job to finsish");
		}
		
	}

	private Collection<Job> getStartedRebalanceJobs() {
		ArrayList<Job> jobs = getJobRepository().list();
		return Collections2.filter(jobs, new Predicate<Job>() {

			@Override
			public boolean apply(Job job) {
				return job.getStatus().getState().toLowerCase().contains("start") 
						&& job.getDescription().toLowerCase().contains("rebalan");
			}
		});
	}

	private JobRepository getJobRepository() {
		return new JobRepository(getSession());
	}
	
	

}
