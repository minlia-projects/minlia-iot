package com.minlia.iot.scope;

/**
 * Created by will on 7/18/17.
 * Http请求媒体类型
 */
public enum HttpMediaType {

  Xml,

  Json,

  /**
   * 当为混合时, 需指定httpExecutor
   */
  Hybrid


}
