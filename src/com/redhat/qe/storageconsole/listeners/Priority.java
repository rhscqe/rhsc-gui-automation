/**
 * 
 */
package com.redhat.qe.storageconsole.listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dustin 
 * Jul 19, 2013
 * 
 * TODO future feature not needed now
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Priority {
	int value() default 1;
}
