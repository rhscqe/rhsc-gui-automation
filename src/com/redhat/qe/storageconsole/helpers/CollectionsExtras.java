/**
 * 
 */
package com.redhat.qe.storageconsole.helpers;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author dustin Dec 20, 2012
 */
public class CollectionsExtras {

	public static <E> E findFirst(Collection<E> unfiltered,
			Predicate<? super E> predicate) {
		Collection<E> results = Collections2.filter(unfiltered, predicate);
		if (results.size() <= 0)
			return null;
		else
			return results.iterator().next();

	}

	public static <E> Collection<E> removeLast(Collection<E> collection) {
		collection.remove(collection.size() - 1);
		return collection;
	}

}
