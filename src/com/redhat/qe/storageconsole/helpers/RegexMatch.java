package com.redhat.qe.storageconsole.helpers;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexMatch {
	String content;
	public  RegexMatch(String content){
		this.content = content;
	}

	public Vector<RegexMatch> find(String regexp){
		Matcher matcher = Pattern.compile(regexp).matcher(content);
		Vector<RegexMatch> vector = new Vector<RegexMatch>(); 
		while(matcher.find()){
			 vector.add(new RegexMatch(content.substring(matcher.start(), matcher.end())));
		}
		return vector;
	}

	public String getText() {
		return content;
	}

}