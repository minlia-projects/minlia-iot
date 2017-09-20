package com.minlia.iot.api;

/**
 * Created by will on 9/17/17.
 */
public interface Api {

  /**
   * 当为sandbox模式时转换为Production类型的RuntimeProfile
   */
  public Api sandbox(Boolean sandbox);
}
