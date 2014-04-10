package com.redhat.qe.storageconsole.helpers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadInput implements Callable<Response> {

	private static Logger LOG = Logger.getLogger(ReadInput.class.getName());
	protected StringBuffer buffer;
	protected InputStream inputStream;
	protected Duration timeout;

	public ReadInput(InputStream inputStream, Duration timeout) {
		this.inputStream = inputStream;
		this.buffer = new StringBuffer();
		this.timeout = timeout;
	}

	public ReadInput(InputStream inputStream) {
		this(inputStream, new Duration(TimeUnit.SECONDS, 10));
	}
	
	public Response call() {
		return call(true);
	}
	
	public Response call(boolean logoutput) {
		long starttime = System.currentTimeMillis();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ( !hasTimedOut(starttime)) {
				if (reader.ready()) {
					buffer.append((char) reader.read());
				}
			}
			if(logoutput)
				LOG.log(Level.INFO, "[shell output] " + getBuffer());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new Response(getBuffer(), getBufferWithEscapes());
	}
	
	public Response clear() {
		return call(false);
	}

	private String stripEscapes(String input) {
		return input.replaceAll("\\033.(\\w+|\\?\\w+){0,1}", "");
	}

	private String getBuffer() {
		return stripEscapes(buffer.toString());
	
	}

	private String getBufferWithEscapes() {
		return buffer.toString();
	}

	private boolean hasTimedOut(long startTime) {
		return (System.currentTimeMillis() - startTime) > TimeUnit.MILLISECONDS.convert(timeout.getInterval(), timeout.getUnits());
	}


	public Response read() {
		return call();
	}


}