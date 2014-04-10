/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dustin 
 * Jan 15, 2013
 */
public class CliVolume {
	String volumeName;
	String type;
	String volumeId;
	String status;
	String numBricks;
	String transportType;
	List<Brick> bricks;
	Map<String,String> options;
	public CliVolume(){
		bricks = new ArrayList();
		options = new HashMap();
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
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the volumeId
	 */
	public String getVolumeId() {
		return this.volumeId;
	}
	/**
	 * @param volumeId the volumeId to set
	 */
	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return this.status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the numBricks
	 */
	public String getNumBricks() {
		return this.numBricks;
	}
	/**
	 * @param numBricks the numBricks to set
	 */
	public void setNumBricks(String numBricks) {
		this.numBricks = numBricks;
	}
	/**
	 * @return the transportType
	 */
	public String getTransportType() {
		return this.transportType;
	}
	/**
	 * @param transportType the transportType to set
	 */
	public void setTransportType(String transportType) {
		this.transportType = transportType;
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
	public void setBricks(List<Brick> bricks) {
		this.bricks = bricks;
	}
	/**
	 * @return the options
	 */
	public Map<String, String> getOptions() {			
		return this.options;
	}
	/**
	 * @param options the options to set
	 */
	public void setOptions(Map<String, String> options) {
		this.options = options;
	}
/**
	 * @param volumeInfo
	 * @return 
	 */
	public static ArrayList<CliVolume> parse(String volumeInfo) {
		ArrayList<CliVolume> results = new ArrayList<CliVolume>();
		CliVolume volume = null;
		List<Brick> bricks = new ArrayList<Brick>();
		for(String line :volumeInfo.split("\n")){
			if (line.contains("Volume Name:")){
				if(volume != null){
					results.add(volume);
				}
				volume = new CliVolume();
				volume.setVolumeName(parseValue(line));
			}else if(line.contains("Type:")){
				volume.setType(parseValue(line));
			}else if(line.contains("Volume ID:")){
				volume.setVolumeId(parseValue(line));
			}else if(line.contains("Status:")){
				volume.setStatus(parseValue(line));
			}else if(line.contains("Number of Bricks:")){
				volume.setNumBricks(parseValue(line));
			}else if(line.contains("Transport-type:")){
				volume.setTransportType(parseValue(line));
			}else if(line.matches("Brick\\d+:.*")){
				bricks.add(Brick.parse(line));
				volume.setBricks(bricks);
			}else if(line.contains("Options Reconfigured:")){
				//do nothing
			}else if(line.contains("Bricks:")){
				//do nothing
			}else if(line.contains("stderr")){
				//do nothing
			}else{			
				volume.getOptions().put(line.split(":")[0],parseValue(line));
			}
		}
		if (volume != null) results.add(volume);
		return results;
	}

	public static String parseValue(String singleLine) {
		return singleLine.replaceAll("^(\\w+(\\.){0,1}(\\s){0,1})+: ","");
	}
 
}
