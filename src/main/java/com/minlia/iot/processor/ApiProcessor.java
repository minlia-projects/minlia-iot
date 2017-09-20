package com.minlia.iot.processor;


import com.minlia.iot.body.ApiHttpRequestBody;
import com.minlia.iot.body.ApiHttpResponseBody;
import com.minlia.iot.body.response.StatefulApiResponseBody;
import com.minlia.iot.context.ApiRuntimeContext;

/**
 * 抽象处理器
 */
public abstract class ApiProcessor<REQUEST extends ApiHttpRequestBody, RESPONSE extends ApiHttpResponseBody> extends
    AbstractApiComponent {


  /**
   * 处理器的主要处理方法
   */
  public abstract StatefulApiResponseBody<RESPONSE> process(
      REQUEST request);

  /**
   * 默认构造方法
   */
  public ApiProcessor() {
  }

  /**
   * API请求上下文
   */
  private ApiRuntimeContext apiRuntimeContext;

  public ApiRuntimeContext getContext() {
    return apiRuntimeContext;
  }

  public ApiProcessor(ApiRuntimeContext apiRuntimeContext) {
    this();
    this.apiRuntimeContext = apiRuntimeContext;
  }


}
