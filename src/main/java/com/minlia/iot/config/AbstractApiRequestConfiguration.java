package com.minlia.iot.config;

import com.minlia.iot.scope.ApiRequestMode;

/**
 * Created by will on 9/19/17.
 * 包含请求模式的请求配置
 */
public abstract class AbstractApiRequestConfiguration {

  private ApiRequestMode apiRequestMode;

  public ApiRequestMode getApiRequestMode() {
    return apiRequestMode;
  }

  public void setApiRequestMode(ApiRequestMode apiRequestMode) {
    this.apiRequestMode = apiRequestMode;
  }
}
