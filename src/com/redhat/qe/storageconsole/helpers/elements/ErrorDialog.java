/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;

import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;

/**
 * @author dustin 
 * May 3, 2013
 */
public class ErrorDialog extends JQueryElement{

	  private static final String ERROR_IMAGE= "iVBORw0KGgoAAAANSUhEUgAAABsAAAAbCAYAAACN1PRVAAAA+klEQVR42mNgwAE+/f8vcvb6ffcJy7b15HYuOGef2vTTJLrqPyUYw5IPHz4Izly7J90spvo3pYbjtezIxRvqgUV9O6ltCYZlIItcMltu0soiuGWgoKOlj1AsA8URrS0CW/bp0ycRWiQGrJadvHLbnR4WgS0D5SNiFJ4xZsApl8bAQJxlMTVTzxFjEQxjk/sPxMRYyGCb3Ii3ZAAZAjPwP5qFZ5DEzhBhIQMx3sdmITqfKJ8RG7noFpJqEUmW4fIhsRaRbBkun1HdMkJxRjXLsAUdvlRKtmX44ohUCxmITRS4EgMhebLyGT6DCMmTlRopLohHLRu1bNBYBgD5D6RxofiQkAAAAABJRU5ErkJggg==";

	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public ErrorDialog(Browser browser) {
		super(new JQuery(String.format(".gwt-DialogBox:has(img[style*='%s'])", ERROR_IMAGE)), browser);
	}
	
	public boolean isVisible(){
		return getJqueryObject().addCall("is",":visible").fetch(getBrowser()).contains("true");
	}
	
	public String getText(){
		return getJqueryObject().addCall("text").fetch(getBrowser());
	}
	

}
