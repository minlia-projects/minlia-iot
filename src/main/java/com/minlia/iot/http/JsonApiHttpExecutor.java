package com.minlia.iot.http;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * HTTP请求发送器
 * 基于XML格式的HTTP请求执行
 */
@Slf4j
public class JsonApiHttpExecutor extends AbstractApiHttpExecutor {

//  @Override
//  public void customConnection(HttpURLConnection connection) {
//    connection.setRequestProperty(HEADER_ACCEPT_PARAMETER, APPLICATION_JSON_VALUE_WITH_ENCODING);
//    connection
//        .setRequestProperty(HEADER_CONTENT_TYPE_PARAMETER, APPLICATION_JSON_VALUE_WITH_ENCODING);
//  }


  @Override
  public List<Header> customHeader() {
    List<Header> headers= Lists.newArrayList();
    headers.add(new BasicHeader(HEADER_ACCEPT_PARAMETER, APPLICATION_JSON_VALUE_WITH_ENCODING));
    headers.add(new BasicHeader(HEADER_CONTENT_TYPE_PARAMETER, APPLICATION_JSON_VALUE_WITH_ENCODING));
    return headers;
  }

}
