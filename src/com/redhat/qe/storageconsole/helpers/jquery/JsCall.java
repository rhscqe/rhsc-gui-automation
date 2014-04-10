/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.jquery;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * @author dustin Dec 19, 2012
 */
public class JsCall implements JsCallable {

	private JsArgument[] args;
	private String methodName;

	/**
	 * @param methodName
	 * @param args
	 */
	public JsCall(String methodName, JsArgument... args) {
		this.methodName = methodName;
		this.args = args;
	}
	
	public Collection<String> argsAsStrings(){
		return Collections2.transform(Arrays.asList(args), new Function<JsArgument, String>(){
			@Override
			public String apply(JsArgument arg){
				return arg.toString();
			}
		});
		
	}

	public String toString() {
		return String.format("%s(%s)", methodName,Joiner.on(",").join(argsAsStrings()));
	}


}
