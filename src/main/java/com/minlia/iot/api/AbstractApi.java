package com.minlia.iot.api;

import com.minlia.iot.config.ApiCredentialConfiguration;
import com.minlia.iot.config.ApiEndpointConfiguration;
import com.minlia.iot.context.ApiRuntimeContext;
import com.minlia.iot.listener.ApiClientListenerAdapter;
import com.minlia.iot.listener.ApiClientListener;
import com.minlia.iot.requestor.HttpRequestMethod;
import java.util.Optional;

/**
 * Created by will on 9/17/17.
 */
public class AbstractApi implements Api {

  public AbstractApi sandbox(Boolean sandbox) {
    apiRuntimeContext.setSandbox(sandbox);
    return this;
  }

  protected ApiRuntimeContext apiRuntimeContext;

  /**
   * 总个数只能为环境的个数, 如Production, Sandbox
   * 如果一个都没有则报错
   */
  public AbstractApi(ApiCredentialConfiguration[] apiCredentialConfiguration,
      ApiEndpointConfiguration[] apiEndpointConfiguration) {

    ApiClientListener listener = new ApiClientListenerAdapter();

    apiRuntimeContext = new ApiRuntimeContext();
    apiRuntimeContext.setListener(listener);
    apiRuntimeContext.setListenerOptional(Optional.ofNullable(listener));

    apiRuntimeContext.setHttpRequestMethod(HttpRequestMethod.POST);

    this.apiRuntimeContext.addAllApiCrenditialConfigurations(apiCredentialConfiguration);
    this.apiRuntimeContext.addAllApiEndpointConfigurations(apiEndpointConfiguration);
  }

  public AbstractApi() {
    throw new RuntimeException("请使用带参构造方法初始化");
  }

}
