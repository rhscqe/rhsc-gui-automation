/**
 * 
 */
package com.redhat.qe.storageconsole.mappper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.redhat.qe.storageconsole.sahi.tasks.StorageSahiVolumeTasks.TaskResult;
import com.redhat.qe.factories.BrickFactory;
import com.redhat.qe.factories.DatacenterFactory;
import com.redhat.qe.factories.VolumeFactory;
import com.redhat.qe.model.Cluster;
import com.redhat.qe.model.Datacenter;
import com.redhat.qe.model.IVolume;
import com.redhat.qe.model.Volume;
import com.redhat.qe.storageconsole.helpers.ParseVersion;
import com.redhat.qe.storageconsole.helpers.RegexMatch;
import com.redhat.qe.storageconsole.te.Brick;
import com.redhat.qe.storageconsole.te.ClientMachine;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 15, 2012
 */
public class VolumeMap extends ClusterMap implements IVolume{
	private String volumeName=null;
	private String volumeType=null;
	private String servers=null;
	private boolean nfsEnabled=false;
	private boolean cifsEnabled=false;
	private int specialCount=0;
	private boolean deleteDir = true;
	// deleteDir when set to false, means that already existing brick directories on hosts should not be deleted
	private List<ClientMachine> clientMachines = null;	
	private String volumeOptionKey = null;
	private String volumeOptionValue = null;
	private String volumeEditOptionValue = null;
	private String volumeAuthAllowValue = null;
	private WarningMessage warningMsg = null;
	private List<VolumeOptionsMap> volumeOptions = null;
	private boolean volumeStart=false;
	private boolean volumeIsTechPreview=false;
	
	public enum VolumeType{
		DISTRIBUTE("Distribute"),
		REPLICATE("Replicate"),
		DISTRIBUTED_REPLICATE("Distributed Replicate"),
		STRIPE("Stripe"),
		DISTRIBUTED_STRIPE("Distributed Stripe");
		
		String type;
		VolumeType(String type){
			this.type = type;
		}
		public String toString(){
			return this.type;
		}
	}
	
	public static class WarningMessage{
		public final static WarningMessage PROTECT_AGAINST_DISK_FAILURE_MOVE_BRICKS = new WarningMessage("To protect against server and disk failures, it is recommended that the bricks of the volume are from different servers.");
		public static final WarningMessage VOLUME_TYPE_CHANGING_FROM_DISTRIBUTED_STRIPE_TO_STRIPE = new WarningMessage("Changing to Striped Volume Type");
		
		private String message;

		public WarningMessage(String message){
			this.message = message;
		}
		public String toString(){ return message; }
		
	}
	
	
	public enum optionType{
		NFS_ENABLE_INFO32("nfs.enable-info32"),
		PERFORMANCE_CACHE_MAX_FILE_SIZE("performance.cache-max-file-size"),
		PERFORMANCE_IO_THREAD_COUNT("performance.io-thread-count"),
		NFS_PRORTS_INSECURE("nfs.ports-insecure"),
		NFS_TRUSTRED_SYNC("nfs.trusted-sync"),
		PERFORMANCE_FLUSH_BEHIND("performance.flush-behind"),
		AUTH_REJECT("auth.reject"),
		NFS_ADDR_NAMELOOKUP("nfs.addr-namelookup"),
		FEATURES_QUOTA_TIMEOUT("features.quota-timeout"),
		PERFORMANCE_CACHE_REFRESH_TIMEOUT("performance.cache-refresh-timeout"),
		FEATURES_LOCK_HEAL("features.lock-heal"),
		NFS_RPC_AUTH_UNIX("nfs.rpc-auth-unix"),
		PERFORMANCE_WRITE_BEHIND_WINDOW_SIZE("performance.write-behind-window-size"),
		NFS_MEM_FACTOR("nfs.mem-factor"),
		NFS_RPC_AUTH_ALLOW("nfs.rpc-auth-allow"),
		DIAGNOSTICS_CLIENT_SYS_LOG_LEVEL("diagnostics.client-sys-log-level"),
		NFS_EXPORT_DIRS("nfs.export-dirs"),
		SERVER_STATEDUMP_PATH("server.statedump-path"),
		DIAGNOSTICS_CLIENT_LOG_LEVEL("diagnostics.client-log-level"),
		DIAGNOSTICS_BRICK_SYS_LOG_LEVEL("diagnostics.brick-sys-log-level"),
		NFS_EXPORT_VOLUMES("nfs.export-volumes"),
		CLUSTER_SELF_HEAL_WINDOW_SIZE("cluster.self-heal-window-size"),
		NFS_REGISTER_WITH_PORTMAP("nfs.register-with-portmap"),
		FEATURES_GRACE_TIMEOUT("features.grace-timeout"),
		CLUSTER_STRIPE_BLOCK_SIZE("cluster.stripe-block-size"),
		NFS_NLM("nfs.nlm"),
		NFS_TRUSTED_WRITE("nfs.trusted-write"),
		PERFORMANCE_HIGH_PRIO_THREADS("performance.high-prio-threads"),
		NFS_VOLUME_ACCESS("nfs.volume-access"),
		NFS_RPC_AUTH_REJECT("nfs.rpc-auth-reject"),
		PERFORMANCE_NORMAL_PRIO_THREADS("performance.normal-prio-threads"),
		PERFORMANCE_READ_AHEAD_PAGE_COUNT("performance.read-ahead-page-count"),
		PERFORMANCE_CACHE_MIN_FILE_SIZE("performance.cache-min-file-size"),
		NFS_PORT("nfs.port"),
		PERFORMANCE_LEAST_PRIO_THREADS("performance.least-prio-threads"),
		AUTH_ALLOW("auth.allow"),
		PERFORMANCE_CACHE_PRIORITY("performance.cache-priority"),
		DIAGNOSTICS_BRICK_LOG_LEVEL("diagnostics.brick-log-level"),
		NFS_RPC_AUTH_NULL("nfs.rpc-auth-null"),
		CLUSTER_DATA_SELF_HEAL_ALGORITHM("cluster.data-self-heal-algorithm"),
		NFS_MOUNT_UDP("nfs.mount-udp"),
		NFS_EXPORT_DIR("nfs.export-dir"),
		PERFORMANCE_LOW_PRIO_THREADS("performance.low-prio-threads"),
		STORAGE_OWNER_UID("storage.owner-uid"),
		STORAGE_OWNER_GID("storage.owner-gid");	
		
		String type;
		optionType(String type){
			this.type = type;
		}
		
		public String toString(){
			return this.type;
		}
	}
	
	/**
	 * @return the volumeName
	 */
	public String getVolumeName() {
		return this.volumeName;
	}
	/**
	 * @param volumeName the volumeName to set
	 */
	public void setVolumeName(String volumeName) {
		this.volumeName = volumeName;
	}
	/**
	 * @return the volumeType
	 */
	public String getVolumeType() {
		return this.volumeType;
	}
	/**
	 * @param volumeType the volumeType to set
	 */
	public void setVolumeType(String volumeType) {
		this.volumeType = volumeType;
	}
	/**
	 * @return the servers
	 */
	public String getServers() {
		return this.servers;
	}
	/**
	 * @param servers the servers to set
	 */
	public void setServers(String servers) {
		this.servers = servers;
	}
	/**
	 * @return the nfsEnabled
	 */
	public boolean isNfsEnabled() {
		return this.nfsEnabled;
	}
	/**
	 * @param nfsEnabled the nfsEnabled to set
	 */
	public void setNfsEnabled(boolean nfsEnabled) {
		this.nfsEnabled = nfsEnabled;
	}
	/**
	 * @return the count
	 */
	public int getSpecialCount() {
		return this.specialCount;
	}
	/**
	 * @param count the count to set
	 */
	public void setSpecialCount(int count) {
		this.specialCount = count;
	}
	
	public String toString(){
		return "[Volume Map] Name:"+this.getVolumeName()+", Type:"+this.getVolumeType()+", Special Count:"+this.getSpecialCount()+", Servers:"+this.getServers()+", Resoure Location:"+this.getResourceLocation();		
	}
	/**
	 * @return the deleteDir
	 */
	public boolean getDeleteDir() {
		return deleteDir;
	}
	/**
	 * @param deleteDir the deleteDir to set
	 */
	public void setDeleteDir(boolean deleteDir) {
		this.deleteDir = deleteDir;
	}
	/**
	 * @return the clientMachines
	 */
	public List<ClientMachine> getClientMachines() {
		return clientMachines;
	}
	/**
	 * @param clientMachines the clientMachines to set
	 */
	public void setClientMachines(List<ClientMachine> clientMachines) {
		this.clientMachines = clientMachines;
	}
	/**
	 * @return the volumeOptionKey
	 */
	public String getVolumeOptionKey() {
		return volumeOptionKey;
	}
	/**
	 * @param volumeOptionKey the volumeOptionKey to set
	 */
	public void setVolumeOptionKey(String volumeOptionKey) {
		this.volumeOptionKey = volumeOptionKey;
	}
	/**
	 * @return the volumeOptionValue
	 */
	public String getVolumeOptionValue() {
		return volumeOptionValue;
	}
	/**
	 * @param volumeOptionValue the volumeOptionValue to set
	 */
	public void setVolumeOptionValue(String volumeEditOptionValue) {
		this.volumeOptionValue = volumeEditOptionValue;
	}
	/**
	 * @return the editVolumeOptionValue
	 */
	public String getVolumeEditOptionValue() {
		return volumeEditOptionValue;
	}
	/**
	 * @param volumeOptionValue the volumeOptionValue to set
	 */
	public void setVolumeEditOptionValue(String volumeEditOptionValue) {
		this.volumeEditOptionValue = volumeEditOptionValue;
	}
	/**
	 * @return the volumeAuthAllowValue
	 */
	public String getVolumeAuthAllowValue() {
		return volumeAuthAllowValue;
	}
	/**
	 * @param volumeAuthAllowValue the volumeAuthAllowValue to set
	 */
	public void setVolumeAuthAllowValue(String volumeAuthAllowValue) {
		this.volumeAuthAllowValue = volumeAuthAllowValue;
	}
	
	/**
	 * @return the warningMsg
	 */
	public WarningMessage getWarningMsg() {
		return this.warningMsg;
	}
	/**
	 * @param warningMsg the warningMsg to set
	 */
	public void setWarningMsg(WarningMessage warningMsg) {
		this.warningMsg = warningMsg;
	}

	/**
	 * @return the cifsEnabled
	 */
	public boolean isCifsEnabled() {
		return this.cifsEnabled;
	}
	/**
	 * @param cifsEnabled the cifsEnabled to set
	 */
	public void setCifsEnabled(boolean cifsEnabled) {
		this.cifsEnabled = cifsEnabled;
	}
	/**
	 * @return the volumeOptions
	 */
	public List<VolumeOptionsMap> getVolumeOptions() {
		return volumeOptions;
	}

	/**
	 * @param volumeOptions the volumeOptions to set
	 */
	public void setVolumeOptions(List<VolumeOptionsMap> volumeOptions) {
		this.volumeOptions = volumeOptions;
	}

	/**
	 * @return the startVolume
	 */
	public boolean getVolumeStart() {
		return volumeStart;
	}
	/**
	 * @param the startVolume to set
	 */
	public void setVolumeStart(boolean volumeStart) {
		this.volumeStart = volumeStart;
	}

	public void setVolumeIsTechPreview(boolean techPreview) {
		this.volumeIsTechPreview = techPreview;
	}
	
	public boolean getVolumeIsTechPreview() {
		return volumeIsTechPreview;
	}
	
	/*
	 * Uses to convert String value to Hash Mapping (Key Value pair)
	 * Example: {key1=value1,value2,value3}{key2=valuse11,value12,value13}{key3=valuer21,value22,value23,value24} to HashMap<String, String>
	 */
	public HashMap<String, String[]> getServerNameToBrickSetsArrayMap(){
		HashMap<String, String[]> result = new HashMap<String, String[]>(); 
		String regexStr = "[\\{.*?\\}]";
		String[] contents = getServers().split(regexStr);
		for(String content : contents){
			if(content.trim().length()>0){
				String[] keyValue = content.split("=");
				result.put(keyValue[0].trim(), keyValue[1].trim().split(","));			
			}			
		}
		return result;
	}
	
	public static interface EachBrickAction{
		public void perform(Server server, String brickSetName, Brick brick) ;
	}

	public static interface EachBrickActionWithResult{
		public TaskResult perform(Server server, String brickSetName, Brick brick) ;
	}
	
	public TaskResult forEachBrickWithResult(final EachBrickActionWithResult action ) {
		try {
			for(Server server : getConfiguredServers()){
				for(String brickSetName : getBrickSetNames( server)){
					for(Brick brick: server.getBricks(brickSetName)){
						TaskResult result = action.perform(server, brickSetName, brick);
						if(result.isStopExecuting()){
							return result;
						}
					}
				}
			}
		} catch (TestEnvironmentConfigException e) {
			 throw new RuntimeException("problem parsing test env configuration file");
		}
		return TaskResult.GOOD;
	}
	public void forEachBrick(final EachBrickAction action ) {
		forEachBrickWithResult(new EachBrickActionWithResult() {
			
			@Override
			public TaskResult perform(Server server, String brickSetName, Brick brick) {
				action.perform(server, brickSetName, brick);
				return TaskResult.GOOD;
			}
		});
		
	}
	
	
	/**
	 * @param serversToBricks
	 * @param server
	 * @return
	 */
	private String[] getBrickSetNames(Server server){
		return getServerNameToBrickSetsArrayMap().get(server.getName());
	}
	/**
	 * @param serversToBricks
	 * @return 
	 * @throws TestEnvironmentConfigException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JAXBException
	 */
	public List<Server> getConfiguredServers() throws TestEnvironmentConfigException{
		return new ArrayList<Server>(TestEnvironmentConfig.getTestEnvironment().getServers(getServerNameToBrickSetsArrayMap().keySet()));
	}
	
	
	public List<Brick> getBricks() {
		final ArrayList<Brick> bricks = new ArrayList<Brick>();
		forEachBrick(new EachBrickAction() {
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				bricks.add(brick);

			}
		});
		return bricks;
	}
	
	private String spaceToUnderscore(String input){
		return input.replaceAll("\\s", "_");
	}
	
	public Volume toVolume(){
		
		Volume volume = new Volume();
		volume.setName(getVolumeName());
		volume.setType(spaceToUnderscore(getVolumeType()));
		if(getVolumeType().toLowerCase().contains("strip"))
			volume.setStripe_count(getSpecialCount());
		else
			volume.setReplicaCount(getSpecialCount());
		
		Cluster cluster = new Cluster();
		cluster.setName(getClusterName());
		cluster.setDescription(getClusterDescription());
		cluster.setMajorVersion(new ParseVersion().getMajorVersion(getClusterCompatibilityVersion()));
		cluster.setMinorVersion(new ParseVersion().getMinorVersion(getClusterCompatibilityVersion()));
		cluster.setDatacenter(new DatacenterFactory().createDefault());
		volume.setCluster(cluster);


		final ArrayList<com.redhat.qe.model.Brick> bricks = new ArrayList<com.redhat.qe.model.Brick>();
		forEachBrick(new EachBrickAction() {
			
			@Override
			public void perform(Server server, String brickSetName, Brick brick) {
				com.redhat.qe.model.Brick thisbrick = new com.redhat.qe.model.Brick();
				thisbrick.setHost(server.toHost());
				thisbrick.setDir(brick.getLocation());
				bricks.add(thisbrick);
				
			}
		});
		volume.setBricks(bricks);
		return volume;
		
	}
	/* (non-Javadoc)
	 * @see com.redhat.qe.model.IVolume#getName()
	 */
	@Override
	public String getName() {
		return getVolumeName();
	}
}
