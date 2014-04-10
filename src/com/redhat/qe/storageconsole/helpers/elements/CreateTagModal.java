package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.mappper.Tag;

public class CreateTagModal extends TagModal{
	public static String DIALOG_NAME = "New Tag";
	

	/**
	 * @param jqueryObj
	 * @param browser
	 */
	public CreateTagModal(Browser browser) {
		super(DIALOG_NAME, browser);
	}
	
	

}
