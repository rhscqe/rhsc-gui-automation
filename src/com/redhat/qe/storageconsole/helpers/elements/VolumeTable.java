/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;

/**
 * @author dustin 
 * Sep 20, 2013
 */
public class VolumeTable extends TableElement{

	/**
	 * 
	 */
	private static final String STATUS_HEADER_TEXT = "";
	public static final String UP_STATUS_ARROW = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAAPCAYAAAAGRPQsAAAAN0lEQVR42mNgGAU0AVo7Gf5TzyAgpNhAmEEwSLaB6AaRbSAug0g2kJBBRBsIVnCGeEy1WB7BAABzAF4Cm2fKZQAAAABJRU5ErkJggg==";
	public static final String DOWN_STATUS_ARROW = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAAPCAYAAAAGRPQsAAAAO0lEQVR42mNgGAUUgZkMDP/PGBsTjUHqCRr4nwhM0CBiDSTaIEIGkmwQLgPJNgjdQIoNQjZwNK3TBgAArKteoioa9eIAAAAASUVORK5CYII=";

	/**
	 * 
	 */
	private static final JQuery SELECTOR = new JQuery("th:contains(Volume Type):last").addCall("closest","table");

	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public VolumeTable( Browser browser) {
		super(SELECTOR,browser);
	}
	
	public String getStatusImageForOfRowIdx(int idx){
		return getRow(idx).getCell(getStatusRowIndex()).getJqueryObject().find("img").addCall("css", "background-image").fetch(getBrowser());
	}

	public boolean isStatusDown(int rowIdx){
		return getStatusImageForOfRowIdx(rowIdx).contains(DOWN_STATUS_ARROW);
	}

	public boolean isStatusUp(int idx){
		return getStatusImageForOfRowIdx(idx).contains(UP_STATUS_ARROW);
	}
	

	public int getStatusRowIndex(){
		 return getHeaders().indexOf(STATUS_HEADER_TEXT);
	}
}
