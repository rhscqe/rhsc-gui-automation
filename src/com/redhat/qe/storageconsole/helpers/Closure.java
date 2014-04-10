package com.redhat.qe.storageconsole.helpers;

import java.util.concurrent.Callable;

public abstract class Closure<T> implements Callable<T> {
  public T perform() {
    return act();
  }

  public T call() {
    return act();
  }
  
  public T execute() {
    return act();
  }

  public abstract T act();
  
}