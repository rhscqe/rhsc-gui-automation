/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.elements;

import java.util.Collection;

import net.sf.sahi.client.Browser;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.mappper.ServerMap;

/**
 * @author dustin 
 * Sep 6, 2013
 */
public class ServerTable extends TableElement{

	public static class Headers{
		private static final String NAME = "Name";
		private static final String HOSTNAME_IP = "Hostname/IP";
	}
	private static final JQuery SELECTOR = new JQuery("table:has(th:contains(Hostname/IP)):has(th:contains(Cluster)):last");

	
	public Row findRow(final ServerMap server){
		final int indexOfName = getHeaders().indexOf(Headers.NAME);
		final int indexOfHost = getHeaders().indexOf(Headers.HOSTNAME_IP);
		Collection<Row> matches = Collections2.filter(getRows(), new Predicate<Row>() {

			@Override
			public boolean apply(Row row) {
				String name = row.getCell(indexOfName).getText();
				String host = row.getCell(indexOfHost).getText();
				ServerMap srvr = server;
				boolean cond1 = name.equals(srvr.getServerName());
				boolean cond2 = host.equals(srvr.getServerHostIP());

				return cond1
						&& cond2;
			}
		});
		return (matches.size() <= 0) ? null : matches.iterator().next();
	}

	public ServerTable( Browser browser) {
		super(SELECTOR, browser);
	}

}