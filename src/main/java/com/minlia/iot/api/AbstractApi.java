package com.minlia.iot.api;


import static com.minlia.iot.code.IotApiCode.NOT_NULL;

import com.minlia.cloud.utils.ApiPreconditions;
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
public abstract class AbstractApi implements Api {

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
    apiRuntimeContext.setEncoding(ApiRuntimeContext.DEFAULT_CHARSET);
    apiRuntimeContext.setListener(listener);
    apiRuntimeContext.setListenerOptional(Optional.ofNullable(listener));

    //默认为POST方法请求
    apiRuntimeContext.setHttpRequestMethod(HttpRequestMethod.POST);

    this.apiRuntimeContext.addAllApiCrenditialConfigurations(apiCredentialConfiguration);
    this.apiRuntimeContext.addAllApiEndpointConfigurations(apiEndpointConfiguration);


    //默认不需要签名
    apiRuntimeContext.setSignatureRequired(Boolean.FALSE);
    //默认不需要签名验证
    apiRuntimeContext.setSignatureVerificationRequired(Boolean.FALSE);

  }

  public AbstractApi() {
  }

}
