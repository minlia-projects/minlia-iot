package com.minlia.iot.api;

import com.minlia.iot.config.ApiCredentialConfiguration;
import com.minlia.iot.config.ApiEndpointConfiguration;
import com.minlia.iot.context.ApiRuntimeContext;
import com.minlia.iot.http.JsonApiHttpExecutor;
import com.minlia.iot.http.XmlApiHttpExecutor;
import com.minlia.iot.listener.ApiClientListener;
import com.minlia.iot.listener.ApiClientListenerAdapter;
import com.minlia.iot.marshal.deserialize.JsonApiDeserializer;
import com.minlia.iot.marshal.deserialize.XmlApiDeserializer;
import com.minlia.iot.marshal.serialize.JsonApiSerializer;
import com.minlia.iot.marshal.serialize.XmlApiSerializer;
import com.minlia.iot.requestor.HttpRequestMethod;
import com.minlia.iot.scope.HttpMediaType;
import java.util.Optional;

/**
 * Created by will on 9/17/17.
 */
public abstract class AbstractXmlApi extends AbstractApi{

  public AbstractXmlApi sandbox(Boolean sandbox) {
    apiRuntimeContext.setSandbox(sandbox);
    return this;
  }

  /**
   * 总个数只能为环境的个数, 如Production, Sandbox
   * 如果一个都没有则报错
   */
  public AbstractXmlApi(ApiCredentialConfiguration[] apiCredentialConfiguration,
      ApiEndpointConfiguration[] apiEndpointConfiguration) {
    super(apiCredentialConfiguration,apiEndpointConfiguration);
    //设置为Xml方式
    apiRuntimeContext.setHttpMediaType(HttpMediaType.Xml);
    apiRuntimeContext.setApiDeserializer(new XmlApiDeserializer<>());
    apiRuntimeContext.setApiSerializer(new XmlApiSerializer<>());
    apiRuntimeContext.setApiHttpExecutor(new XmlApiHttpExecutor());

  }


}
