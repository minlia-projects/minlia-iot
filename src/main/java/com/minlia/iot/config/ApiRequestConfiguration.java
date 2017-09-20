package com.minlia.iot.config;

import com.minlia.iot.scope.ApiRequestMode;

/**
 * Created by will on 9/10/17.
 * API请求配置
 * 保存配置信息
 * API地址
 * 是否沙箱模式等信息
 */
public interface ApiRequestConfiguration {

  /**
   * 请求模式, 沙箱还是正式环境
   * @return
   */
  public ApiRequestMode getApiRequestMode();

}
