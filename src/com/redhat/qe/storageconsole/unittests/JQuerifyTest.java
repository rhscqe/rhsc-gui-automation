/**
 * 
 */
package com.redhat.qe.storageconsole.unittests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.redhat.qe.storageconsole.helpers.elements.TableElement;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JsString;

/**
 * @author dustin 
 * Feb 19, 2013
 */
public class JQuerifyTest {
	
	@Test
	public void test(){
		assertTrue(new JQuery("#hi").getCallStack().getCallStack().toString().contains("jQuery"));
	}
	
	
	@Test
	public void callTest(){
		assertEquals(new JQuery("x").toString(),"jQuery(\"x\")");
	}
	
	@Test
	public void jqueryLocator(){
		
		TableElement table = new TableElement("x", null);
		assertEquals(table.getJqueryObject().toString(), "jQuery(\"x\")".toString());
	}
	@Test
	public void findcell(){
		TableElement table = new TableElement("x", null);
		assertEquals(table.findCell("cell").getJqueryObject().toString(), "jQuery(\"x\").find(\"> tr > td:text('cell')\")");
	}
	@Test
	public void find(){
		TableElement table = new TableElement("x", null);
		table.getJqueryObject().addCall("find", new JsString("something"));
	}
	
	@Test
	public void blah(){
		assertEquals(new JsString( "%s", "hi").toString(), "\"hi\"");
	}
	
	@Test
	public void strfmt(){
		assertEquals(String.format("%s",new String[]{"hi"}), "hi");
	}
	

}
