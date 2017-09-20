package com.minlia.iot.http;

import com.minlia.iot.requestor.HttpRequestMethod;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 抽象的HTTP请求发送器
 */
@Slf4j
public abstract class AbstractApiHttpExecutor implements ApiHttpExecutor {


  @Override
  public HttpResponse execute(String url, HttpRequestMethod method, String payload,String encoding) {

    if(StringUtils.isEmpty(encoding)){
      encoding=DEFAULT_ENCODING;
    }
    try {
      URL url1 = new URL(url);
      log.debug("Post to {}", url);

      HttpURLConnection connection = null;
      if (url.startsWith("https")) {
        connection = (HttpsURLConnection) url1.openConnection();
      } else {
        connection = (HttpURLConnection) url1.openConnection();
      }

      // 发送POST请求必须设置如下两行
      connection.setDoOutput(true);
      connection.setDoInput(true);

      connection.setRequestProperty(HEADER_CONNECTION_PARAMETER, HEADER_CONNECTION_KEEP_ALIVE_VALUE);
      connection.setRequestProperty(HEADER_USER_AGENT_PARAMETER,HEADER_USER_AGENT_VALUE);

      customConnection(connection);

      connection.setRequestMethod(method.name());

//      connection.connect();

      log.debug("Connection: {}", connection);
      log.debug("Send http execute: {} {}", url, connection.getRequestMethod());
      log.debug("Payload: {}", payload);
      IOUtils.write(payload, connection.getOutputStream(), encoding);

      HttpResponse response = new HttpResponse();
      response.setHttpStatus(connection.getResponseCode());
      if (response.getHttpStatus() == 200) {
        List<String> lines = IOUtils.readLines(connection.getInputStream(), encoding);
        StringBuilder sb = new StringBuilder();
        lines.forEach(sb::append);
        log.debug("Response Payload: {}", sb.toString());
        response.setContent(sb.toString());
      } else {
        List<String> lines = IOUtils.readLines(connection.getErrorStream(), encoding);
        StringBuilder sb = new StringBuilder();
        lines.forEach(sb::append);
        log.debug("Response Error Payload: {}", sb.toString());
        response.setErrorMassage(sb.toString());
      }

      return response;

    } catch (MalformedURLException e) {
      log.debug("Invalid URL for reuqest {}",e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
