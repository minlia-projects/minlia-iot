package com.minlia.iot.context;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.minlia.iot.config.ApiEndpointConfiguration;
import com.minlia.iot.marshal.serialize.ApiSerializer;
import com.minlia.iot.body.ApiHttpRequestBody;
import com.minlia.iot.body.ApiHttpResponseBody;
import com.minlia.iot.config.ApiCredentialConfiguration;
import com.minlia.iot.http.ApiHttpExecutor;
import com.minlia.iot.listener.ApiClientListener;
import com.minlia.iot.marshal.deserialize.ApiDeserializer;
import com.minlia.iot.marshal.deserialize.JsonApiDeserializer;
import com.minlia.iot.marshal.deserialize.XmlApiDeserializer;
import com.minlia.iot.requestor.ApiHttpRequestor;
import com.minlia.iot.requestor.HttpRequestMethod;
import com.minlia.iot.scope.ApiRequestMode;
import com.minlia.iot.scope.HttpMediaType;
import com.minlia.iot.signature.sign.SignatureProcessor;
import com.minlia.iot.signature.verification.SignatureVerificationProcessor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by will on 9/10/17.
 * API运行时上下文
 * 存储一些运行时的参数
 */
public class ApiRuntimeContext<REQUEST extends ApiHttpRequestBody, RESPONSE extends ApiHttpResponseBody> {

  public static final String DEFAULT_CHARSET="UTF-8";
  public static final String DEFAULT_DELIMITER = "&";


  private Map<String, Object> availableApiCredentialConfigurations = Maps.newConcurrentMap();

  private Map<String, Object> availableApiEndpointConfigurations = Maps.newConcurrentMap();

  //ApiCredentialConfiguration
  public void addAllApiCrenditialConfigurations(ApiCredentialConfiguration... contexts) {
    //先清除, 再添加到对应的环境
    this.availableApiCredentialConfigurations.clear();
    for (ApiCredentialConfiguration context : contexts) {
      availableApiCredentialConfigurations.put(context.getApiRequestMode().name(), context);
    }
  }

  public ApiCredentialConfiguration getPreferApiCredentialConfiguration() {
    ApiCredentialConfiguration apiCredentialConfiguration = null;

    ApiRequestMode mode = getPreferedApiRequestMode();
    if (ApiRequestMode.SANDBOX.equals(mode)) {
      apiCredentialConfiguration = (ApiCredentialConfiguration) availableApiCredentialConfigurations
          .get(ApiRequestMode.SANDBOX.name());
    } else {
      apiCredentialConfiguration = (ApiCredentialConfiguration) availableApiCredentialConfigurations
          .get(ApiRequestMode.PRODUCTION.name());
    }
    if (null == apiCredentialConfiguration) {
      throw
          new RuntimeException("定义了" + mode.name() + "的运行模式,可是没能找到与之对应的配置, 请先配置或修改运行模式");
    }
    return apiCredentialConfiguration;
  }

  public ApiCredentialConfiguration getApiCredentialConfigurationByMode(ApiRequestMode mode) {
    if (null != mode && mode.equals(ApiRequestMode.SANDBOX)) {
      return (ApiCredentialConfiguration) availableApiCredentialConfigurations
          .get(ApiRequestMode.SANDBOX.name());
    } else {
      return (ApiCredentialConfiguration) availableApiCredentialConfigurations
          .get(ApiRequestMode.PRODUCTION.name());
    }
  }


  //apiEndpointConfiguration
  public void addAllApiEndpointConfigurations(ApiEndpointConfiguration... contexts) {
    //先清除, 再添加到对应的环境
    this.availableApiEndpointConfigurations.clear();
    //需要区分出来成为2个对象 一个凭证, 另一个端点
    for (ApiEndpointConfiguration context : contexts) {
      availableApiEndpointConfigurations.put(context.getApiRequestMode().name(), context);
    }
  }

  public ApiEndpointConfiguration getPreferApiEndpointConfiguration() {
//   if(ApiRequestMode.SANDBOX.equals(getPreferedApiRequestMode())){
//      return (ApiEndpointConfiguration) availableApiEndpointConfigurations.get(ApiRequestMode.SANDBOX);
//    } else {
//      return (ApiEndpointConfiguration) availableApiEndpointConfigurations.get(ApiRequestMode.PRODUCTION);
//    }

    ApiEndpointConfiguration apiEndpointConfiguration = null;

    ApiRequestMode mode = getPreferedApiRequestMode();
    if (ApiRequestMode.SANDBOX.equals(mode)) {
      apiEndpointConfiguration = (ApiEndpointConfiguration) availableApiEndpointConfigurations
          .get(ApiRequestMode.SANDBOX.name());
    } else {
      apiEndpointConfiguration = (ApiEndpointConfiguration) availableApiEndpointConfigurations
          .get(ApiRequestMode.PRODUCTION.name());
    }
    if (null == apiEndpointConfiguration) {
      throw
          new RuntimeException("定义了" + mode.name() + "的运行模式,可是没能找到与之对应的配置, 请先配置或修改运行模式");
    }
    return apiEndpointConfiguration;

  }

  public ApiEndpointConfiguration getApiEndpointConfigurationByMode(ApiRequestMode mode) {
    if (null != mode && mode.equals(ApiRequestMode.SANDBOX)) {
      return (ApiEndpointConfiguration) availableApiEndpointConfigurations
          .get(ApiRequestMode.SANDBOX.name());
    } else {
      return (ApiEndpointConfiguration) availableApiEndpointConfigurations
          .get(ApiRequestMode.PRODUCTION.name());
    }
  }

  private Object getFieldValue(Object obj, String fieldName)
      throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
    if (obj != null) {
      Class classOrSuperclass = obj.getClass();

      while (classOrSuperclass != null) {
        try {
          Field field = classOrSuperclass.getDeclaredField(fieldName);
          if (!field.isAccessible()) {
            field.setAccessible(true);
          }

          return field.get(obj);
        } catch (NoSuchFieldException var4) {
          classOrSuperclass = classOrSuperclass.getSuperclass();
        }
      }
    }
    return null;
  }

  public String getPreferedEndpoint() {
    ApiEndpointConfiguration apiEndpointConfiguration = getApiEndpointConfigurationByMode(
        getPreferedApiRequestMode());
    String result = "";
    String apiScopeSource=apiScope;
    String apiScopeConverted="";
    try {
      apiScopeConverted = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, apiScopeSource);
      result = (String) getFieldValue(apiEndpointConfiguration, apiScopeConverted);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    if(StringUtils.isEmpty(result)){
      throw new RuntimeException("找不到当前ApiScope的配置项,请检查ApiEndpointConfiguration与ApiScopes中定义的值是否相等 如: queryEndpoint 会被自动转换为 QUERY_ENDPOINT, ApiScopes中定义的枚举为:" +apiScopeSource+", 请确保ApiEndpointConfiguration中有"+apiScopeConverted+"的属性");
    }
    return result;
  }


  private ApiRequestMode getPreferedApiRequestMode() {
    if (null != sandbox && sandbox) {
      return ApiRequestMode.SANDBOX;
    } else {
      return ApiRequestMode.PRODUCTION;
    }
  }

//  private ApiRequestConfiguration preferApiRequestContext;

  /**
   * 当为sandbox模式时改变URL
   */
  private Boolean sandbox;

//  private String sandboxReplacement;

  /**
   * 是否是沙箱模式请求
   */
  public Boolean isSandbox() {
    return sandbox;
  }

  /**
   * 请求api的上游endpoint
   */
  private String endpoint;


  public String getEndpoint() {

    //根据Scope从当前请求上下文中取得 customerEnter
    if (null != apiScope) {
      System.out.println("From apiScope");
    } else {
      throw
          new RuntimeException("No scope found");
    }

    return "";
  }

  private String apiScope;

//  private ApiScopes scope;


  /**
   * 请求的编码类型
   */
  private String encoding;
  //  /**
//   * 返回体类型
//   */
  private Class<RESPONSE> businessResponseBodyClass;

  private Class<?> statefulResponseBodyClass;

  /**
   * 监听器
   */
  private ApiClientListener listener;

  @Getter
  private Optional<ApiClientListener> listenerOptional;

  /**
   * 请求媒体类型
   */
  private HttpMediaType httpMediaType;

  /**
   * 请求方法 POST GET
   */
  private HttpRequestMethod httpRequestMethod;

  private ApiHttpExecutor apiHttpExecutor;
  /**
   * 序列化器
   */
  protected ApiSerializer<REQUEST> apiSerializer;

  /**
   * 反序列化器
   */
  protected ApiDeserializer<RESPONSE> apiDeserializer;


  public ApiDeserializer<RESPONSE> getApiDeserializer() {

    //当有指定时直接返回
    if (null != apiDeserializer) {
      return apiDeserializer;
    }

    ApiDeserializer<RESPONSE> ret = null;

    //当为空的时候进行智能选择
    if (null != httpMediaType) {

      switch (httpMediaType) {
        case Xml:
          ret = new XmlApiDeserializer();
          break;

        case Json:
          ret = new JsonApiDeserializer();
          break;

        case Hybrid: {
          throw new RuntimeException("指定了使用了非标准的请求方式, 请指定反序列化器");
//          ret = apiDeserializer;
//          break;

        }

        default:

          break;

      }
    }

    return ret;
  }

  /**
   * API请求器
   */
  protected ApiHttpRequestor apiHttpRequestor;


  /**
   * 签名处理器
   */
  protected SignatureVerificationProcessor signatureVerificationProcessor;
  protected SignatureProcessor signatureProcessor;

  /**
   * 是否签名验证必须
   */
  protected Boolean isSignatureVerificationRequired;
  protected Boolean isSignatureRequired;


  public Boolean getSignatureVerificationRequired() {
    return isSignatureVerificationRequired;
  }

  public void setSignatureVerificationRequired(Boolean signatureVerificationRequired) {
    isSignatureVerificationRequired = signatureVerificationRequired;
  }

  public Boolean getSandbox() {
    return sandbox;
  }

  public void setSandbox(Boolean sandbox) {
    this.sandbox = sandbox;
  }


  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public Class<RESPONSE> getBusinessResponseBodyClass() {
    return businessResponseBodyClass;
  }

  public void setBusinessResponseBodyClass(Class<RESPONSE> businessResponseBodyClass) {
    this.businessResponseBodyClass = businessResponseBodyClass;
  }

  public Class<?> getStatefulResponseBodyClass() {
    return statefulResponseBodyClass;
  }

  public void setStatefulResponseBodyClass(Class<?> statefulResponseBodyClass) {
    this.statefulResponseBodyClass = statefulResponseBodyClass;
  }

  public ApiClientListener getListener() {
    return listener;
  }

  public void setListener(ApiClientListener listener) {
    this.listener = listener;
  }

  public Optional<ApiClientListener> getListenerOptional() {
    return listenerOptional;
  }

  public void setListenerOptional(
      Optional<ApiClientListener> listenerOptional) {
    this.listenerOptional = listenerOptional;
  }


  public HttpMediaType getHttpMediaType() {
    return httpMediaType;
  }

  public void setHttpMediaType(HttpMediaType httpMediaType) {
    this.httpMediaType = httpMediaType;
  }

  public HttpRequestMethod getHttpRequestMethod() {
    return httpRequestMethod;
  }

  public void setHttpRequestMethod(HttpRequestMethod httpRequestMethod) {
    this.httpRequestMethod = httpRequestMethod;
  }

  public ApiSerializer<REQUEST> getApiSerializer() {
    return apiSerializer;
  }

  public void setApiSerializer(
      ApiSerializer<REQUEST> apiSerializer) {
    this.apiSerializer = apiSerializer;
  }

  public void setApiDeserializer(
      ApiDeserializer<RESPONSE> apiDeserializer) {
    this.apiDeserializer = apiDeserializer;
  }

  public ApiHttpRequestor getApiHttpRequestor() {
    return apiHttpRequestor;
  }

  public void setApiHttpRequestor(ApiHttpRequestor apiHttpRequestor) {
    this.apiHttpRequestor = apiHttpRequestor;
  }

  public SignatureVerificationProcessor getSignatureVerificationProcessor() {
    return signatureVerificationProcessor;
  }

  public void setSignatureVerificationProcessor(
      SignatureVerificationProcessor signatureVerificationProcessor) {
    this.signatureVerificationProcessor = signatureVerificationProcessor;
  }


  public ApiHttpExecutor getApiHttpExecutor() {
    return apiHttpExecutor;
  }

  public void setApiHttpExecutor(ApiHttpExecutor apiHttpExecutor) {
    this.apiHttpExecutor = apiHttpExecutor;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

//  public String getApiScope() {
//    return apiScope;
//  }

  public void setApiScope(String apiScope) {
    this.apiScope = apiScope;
  }

  public SignatureProcessor getSignatureProcessor() {
    return signatureProcessor;
  }

  public void setSignatureProcessor(
      SignatureProcessor signatureProcessor) {
    this.signatureProcessor = signatureProcessor;
  }

  public Boolean getSignatureRequired() {
    return isSignatureRequired;
  }

  public void setSignatureRequired(Boolean signatureRequired) {
    isSignatureRequired = signatureRequired;
  }

  //  public String getSandboxReplacement() {
//    return sandboxReplacement;
//  }
//
//  public void setSandboxReplacement(String sandboxReplacement) {
//    this.sandboxReplacement = sandboxReplacement;
//  }

//  public ApiScopes getScope() {
//    return scope;
//  }
//
//  public void setScope(ApiScopes scope) {
//    this.scope = scope;
//  }
//
//  public ApiRequestConfiguration getPreferApiRequestContext() {
//    return preferApiRequestContext;
//  }
//
//  public void setPreferApiRequestContext(
//      ApiRequestConfiguration preferApiRequestContext) {
//    this.preferApiRequestContext = preferApiRequestContext;
//  }
}
