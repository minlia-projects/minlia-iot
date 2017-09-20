package com.minlia.iot.http;

import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;

/**
 * HTTP请求发送器
 *
 *基于XML格式的HTTP请求执行
 */
@Slf4j
public class XmlApiHttpExecutor extends AbstractApiHttpExecutor {

  @Override
  public void customConnection(HttpURLConnection connection) {
    connection.setRequestProperty(HEADER_ACCEPT_PARAMETER,HEADER_ACCEPT_ALL_ACCEPTED_VALUE);
    connection.setRequestProperty(HEADER_CONTENT_TYPE_PARAMETER, APPLICATION_XML_VALUE_WITH_ENCODING);
  }

}
