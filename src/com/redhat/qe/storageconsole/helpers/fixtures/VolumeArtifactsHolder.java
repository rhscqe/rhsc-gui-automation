package com.redhat.qe.storageconsole.helpers.fixtures;

import java.util.ArrayList;

import com.redhat.qe.model.Cluster;
import com.redhat.qe.model.Host;
import com.redhat.qe.model.Volume;
import com.redhat.qe.storageconsole.mappper.VolumeMap;

public  class VolumeArtifactsHolder{
	
	private Volume volume;
	private VolumeMap volumeMap;
	private ArrayList<Host> hosts;
	private Cluster cluster;
	
	
	/**
	 * @param volume
	 * @param volumeMap
	 * @param cluster 
	 * @param hosts 
	 */
	public VolumeArtifactsHolder(Volume volume, VolumeMap volumeMap, ArrayList<Host> hosts, Cluster cluster) {
		super();
		this.volume = volume;
		this.volumeMap = volumeMap;
		this.hosts = hosts;
		this.cluster = cluster;
	}
	public Volume getVolume() {
		return this.volume;
	}
	public void setVolume(Volume volume) {
		this.volume = volume;
	}
	public VolumeMap getVolumeMap() {
		return this.volumeMap;
	}
	public void setVolumeMap(VolumeMap volumeMap) {
		this.volumeMap = volumeMap;
	}
	public ArrayList<Host> getHosts() {
		return this.hosts;
	}
	public void setHosts(ArrayList<Host> hosts) {
		this.hosts = hosts;
	}
	public Cluster getCluster() {
		return this.cluster;
	}
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	
	
	
	
}