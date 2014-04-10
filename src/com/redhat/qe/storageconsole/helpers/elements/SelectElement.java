/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

public class SelectElement extends SahiElement{

	public SelectElement(Browser browser, ElementStub element) {
		super(browser,element);
	}

	public List<ElementStub> getOptions(){
		int numOptions = getNumOptions();
		ArrayList<ElementStub> results = new ArrayList<ElementStub>();
		for(int i = 0; i< numOptions; i++){
			 ElementStub option = getBrowser().option("[" + i +"]").in(getBrowser().select("VolumeParameterPopupView_keyListBox"));
			 results.add(option);
		}
		return results;
	}
	
	public Collection<String> getOptionValues(){
		return Collections2.transform(getOptions(), new Function<ElementStub,String>(){
			public String apply(ElementStub elem){
				return elem.getValue();
			}
		});
	}
	public Collection<String> getOptionInnerTexts(){
		return Collections2.transform(getOptions(), new Function<ElementStub,String>(){
			public String apply(ElementStub elem){
				return elem.getText();
			}
		});
	}

	public int getNumOptions() {
		int numOptions = getBrowser().option("").in(getElement()).countSimilar();
		return numOptions;
	}
	
	
}
