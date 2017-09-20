package com.minlia.iot.marshal.deserialize;

import com.minlia.iot.body.response.StatefulApiResponseBody;
import com.minlia.iot.context.ApiRuntimeContext;
import com.minlia.iot.marshal.ApiMarshalWrappedBody;
import com.minlia.iot.processor.AbstractApiComponent;
import com.minlia.iot.signature.verification.SignatureVerificationProcessor;
import com.minlia.iot.body.SignatureVerificationBody;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于注解的反序列化器
 */
@Slf4j
public abstract class AbstractAnnotationApiDeserializer<RESPONSE> extends
    AbstractApiComponent implements
    ApiDeserializer<RESPONSE> {

  public abstract StatefulApiResponseBody<RESPONSE> execute(ApiMarshalWrappedBody body,
      ApiRuntimeContext context);

  /**
   * 反序列化当前请求的返回内容
   */
  @Override
  public StatefulApiResponseBody<RESPONSE> deserialize(ApiMarshalWrappedBody body,
      ApiRuntimeContext context) {
    log.debug("Response {}", body.getRaw());

    StatefulApiResponseBody<RESPONSE> ret = execute(body, context);

    postconditions(body, context, ret);

    return ret;

  }

  /**
   * 在反序列化后,进行签名校验, 不通过直接抛出异常
   */
  private void postconditions(ApiMarshalWrappedBody body, ApiRuntimeContext context,
      StatefulApiResponseBody<RESPONSE> deserialized) {
    log.debug("Precondition signature verification before deserialize");

    if (null != context.getSignatureVerificationRequired() && context
        .getSignatureVerificationRequired()) {
      Boolean signatureValid = false;

      SignatureVerificationProcessor processor = context.getSignatureVerificationProcessor();

      if (null == processor) {
        throw new RuntimeException("指定了需要校验签名, 但是无法找到合适的签名校验处理器, 请指定签名校验处理器");
      }

      //从上下文中获取签名处理器
      SignatureVerificationBody signatureVerificationBody = new SignatureVerificationBody();
      signatureVerificationBody.setRaw(body.getRaw());
      signatureVerificationBody.setEntityClass(body.getBusinessResponseBodyClass());
      signatureValid = context.getSignatureVerificationProcessor()
          .verify(signatureVerificationBody);

      if (!signatureValid) {
        throw new RuntimeException("验证反回数据的签名无法通过, 请确认验签处理是否正确");
      }
    }


  }


}
