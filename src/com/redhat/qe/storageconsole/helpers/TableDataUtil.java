/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;

/**
 * @author dustin 
 * Feb 21, 2013
 */
public class TableDataUtil {
	public static Map<String,String> findFirstRow(List<HashMap<String,String>> table,final String columnName, final String text ){
		return CollectionUtils.findFirst(table, new Predicate<HashMap<String,String>>(){
			public boolean apply(HashMap<String,String> row){
				return row.get(columnName).equals(text);
			}
		});
	}

	

}
