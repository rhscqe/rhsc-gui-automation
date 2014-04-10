package com.redhat.qe.storageconsole.helpers.elements;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.mappper.Tag;

public class TagModal extends Dialog{
	/**
	 * @param title
	 * @param browser
	 */
	public TagModal(String title, Browser browser) {
		super(title, browser);
	}

	public static String DIALOG_NAME = "New Tag";
	


	public JQueryElement getNameField(){
		return getField("Name");
	}
	
	public JQueryElement getDescriptionField(){
		return getField("Description");
		
	}
	
	public void fillNewTagModal(Tag tag) {
		ElementStub nameField = getNameField().getElementStub();
		nameField.setValue(tag.getName());
		getDescriptionField().getElementStub().setValue(tag.getDescription());
	}

}
