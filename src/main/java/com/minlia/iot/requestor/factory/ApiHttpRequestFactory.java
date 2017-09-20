package com.minlia.iot.requestor.factory;

import com.minlia.iot.context.ApiRuntimeContext;
import com.minlia.iot.http.ApiHttpExecutor;
import com.minlia.iot.http.FormDataWithJsonApiHttpExecutor;
import com.minlia.iot.http.XmlApiHttpExecutor;
import com.minlia.iot.http.JsonApiHttpExecutor;
import com.minlia.iot.scope.HttpMediaType;
import lombok.Data;

/**
 * Created by will on 9/12/17.
 * 根据请求类型决定使用哪种请求器
 *
 * 区别在于请求时封装的参数不同
 */
@Data
public class ApiHttpRequestFactory {


  public static ApiHttpExecutor create(HttpMediaType mediaType, ApiRuntimeContext context) {

    ApiHttpExecutor request = null;

    //取指定的请求器
    if (null != context.getApiHttpExecutor()) {
      request = context.getApiHttpExecutor();
    } else {
      //进行默认构造, 当还是没有时抛出异常
      switch (mediaType) {
        case Json:
          request = new JsonApiHttpExecutor();
          break;

        case Xml:
          request = new XmlApiHttpExecutor();
          break;

        case Hybrid:
          request = new FormDataWithJsonApiHttpExecutor();
          break;

        default:
          throw new RuntimeException("请指定HTTP请求器");
      }
    }
    return request;
  }


}
