package com.minlia.iot.processor;

/**
 * Created by will on 9/11/17.
 */
public interface DataProcessor<T> {

  public <T> T process(T body);

}
