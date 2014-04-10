/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dustin 
 * Feb 21, 2013
 */
public class ListUtil {
	public static <T> Map<T, T> join(List<T> a,List<T> b) {
		assert a.size() == b.size();
		Map<T, T> rowData = new HashMap<T, T>();
		for (int j = 0; j < a.size(); j++) {
			rowData.put(a.get(j), b.get(j));
		}
		return rowData;
	}
	
	public static <T> HashMap<T, T> joinHashMap(List<T> a,List<T> b) {
		return new HashMap<T,T>(join(a,b));
	}
}
