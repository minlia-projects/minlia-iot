package com.minlia.iot.signature.sign;

import com.minlia.iot.body.ApiHttpRequestBody;
import com.minlia.iot.context.ApiRuntimeContext;

/**
 * Created by will on 9/10/17.
 * 签名处理器
 */
public interface SignatureProcessor<REQUEST extends ApiHttpRequestBody> {

  /**
   * 进行签名
   * 默认情况绑定给请求体中的sign属性
   *
   * 如有需要可以返回此signature值供外部使用
   */
  public String sign(REQUEST requestBody,ApiRuntimeContext context);

}
