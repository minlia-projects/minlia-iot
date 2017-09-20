package com.minlia.iot.processor;

import com.minlia.iot.body.ApiHttpRequestBody;
import com.minlia.iot.body.ApiHttpResponseBody;
import com.minlia.iot.body.response.StatefulApiResponseBody;
import com.minlia.iot.context.ApiRuntimeContext;
import com.minlia.iot.http.ApiHttpExecutor;
import com.minlia.iot.http.HttpResponse;
import com.minlia.iot.marshal.ApiMarshalWrappedBody;
import com.minlia.iot.requestor.factory.ApiHttpRequestFactory;
import com.minlia.iot.scope.HttpMediaType;
import com.minlia.iot.signature.sign.SignatureProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于注解的处理器
 */
@Slf4j
public abstract class AbstractAnnotationApiProcessor<REQUEST extends ApiHttpRequestBody, RESPONSE extends ApiHttpResponseBody> extends
    ApiProcessor<REQUEST, RESPONSE> {

  /**
   * 处理流程
   * 仅作为默认流程组装, 具体实现需要根据实现类确定
   */
  @Override
  public StatefulApiResponseBody<RESPONSE> process(
      REQUEST request) {
    try {

      //首先进行签名, 如有需要
      if(null !=getContext().getSignatureRequired() && getContext().getSignatureRequired()){
        SignatureProcessor signatureProcessor=getContext().getSignatureProcessor();
        if(null==signatureProcessor){
          throw new RuntimeException("请指定签名处理器");
        }else{
          //进行签名
          signatureProcessor.sign(request,getContext());
        }
      }

      //根据请求媒体类型决定使用哪个HTTP请求器
      HttpMediaType httpMediaType = getContext().getHttpMediaType();

      //序列化
      String content = getContext().getApiSerializer().serialize(request,httpMediaType);

      //创建请求器 实际是在设置HTEADER
      ApiHttpExecutor apiHttpExecutor = ApiHttpRequestFactory.create(httpMediaType,getContext());

      //请求的上游api地址
      String endpoint=getContext().getPreferedEndpoint();

      //执行Api请求
      HttpResponse httpResponse = apiHttpExecutor
          .execute(endpoint, getContext().getHttpRequestMethod(), content,getContext().getEncoding());

      //首先进行HTTP请求结果确认, 如果有报错则直接抛出
      if (httpResponse.isOK()) {
        ApiMarshalWrappedBody apiMarshalWrappedBody = ApiMarshalWrappedBody.builder()
            .raw(httpResponse.getContent())
            .httpMediaType(getContext().getHttpMediaType())
            .businessResponseBodyClass(getContext().getBusinessResponseBodyClass())
            .statefulResponseBodyClass(getContext().getStatefulResponseBodyClass())
            .build();

        StatefulApiResponseBody<RESPONSE> result= getContext().getApiDeserializer()
            .deserialize(apiMarshalWrappedBody,getContext());
        return result;

      } else {
        // http请求失败
        getContext().getListenerOptional().ifPresent(
            listener -> getContext().getListener().httpFailed(getContext().getPreferApiEndpointConfiguration(), httpResponse));

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public AbstractAnnotationApiProcessor(ApiRuntimeContext apiRuntimeContext) {
    super(apiRuntimeContext);
  }



}
