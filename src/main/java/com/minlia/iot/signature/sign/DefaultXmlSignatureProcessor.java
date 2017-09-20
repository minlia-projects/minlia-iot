package com.minlia.iot.signature.sign;

import com.minlia.iot.body.ApiHttpRequestBody;
import com.minlia.iot.context.ApiRuntimeContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by will on 9/10/17.
 * 使用XML属性进行排序后签名
 */
@Slf4j
public class DefaultXmlSignatureProcessor<REQUEST extends ApiHttpRequestBody> implements SignatureProcessor<REQUEST> {

  @Override
  public String sign(REQUEST requestBody,ApiRuntimeContext context) {
    throw new RuntimeException("请指定具体的签名处理器");
  }


}
