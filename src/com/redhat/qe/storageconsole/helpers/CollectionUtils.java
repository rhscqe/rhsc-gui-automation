/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author dustin 
 * Feb 21, 2013
 */
public class CollectionUtils {

	public static <T> T findFirst(Collection<T> collection, Predicate<T> predicate){
		Collection<T> filterdList = Collections2.filter(collection, predicate);
		if(filterdList.size() <= 0){
			return null;
		}else{
			return filterdList.iterator().next();
		}
	}
}
