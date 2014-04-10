package com.redhat.qe.storageconsole.listeners.depend;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



@Retention(RetentionPolicy.RUNTIME) // Make this annotation accessible at runtime via reflection.
public @interface HaltAllSubsequentOnFailure {
}