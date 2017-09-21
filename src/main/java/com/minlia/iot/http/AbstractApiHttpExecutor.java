package com.minlia.iot.http;

import com.minlia.iot.requestor.HttpRequestMethod;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.ssl.SSLContexts;

/**
 * 抽象的HTTP请求发送器
 */
@Slf4j
public abstract class AbstractApiHttpExecutor implements ApiHttpExecutor {


  @SuppressWarnings("deprecation")
  @Override
  public HttpResponse execute(String url, HttpRequestMethod method, String payload,String encoding) {



    if(StringUtils.isEmpty(encoding)){
      encoding=DEFAULT_ENCODING;
    }


    //开始


    HttpResponse result = new HttpResponse();


    final TrustStrategy acceptingTrustStrategy = (certificate, authType) -> true;
    SSLContext sslContext=null;


    try {
      sslContext = SSLContexts
          .custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    }

    final CloseableHttpAsyncClient client = HttpAsyncClients.custom().setSSLHostnameVerifier(
        SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).setSSLContext(sslContext).build();
    client.start();

    RequestBuilder requestBuilder= RequestBuilder.create(method.name());

    requestBuilder    .setUri(url);

    StringEntity params = new StringEntity(payload, encoding);
    requestBuilder.setEntity(params);

    requestBuilder.setHeader(HEADER_CONNECTION_PARAMETER, HEADER_CONNECTION_KEEP_ALIVE_VALUE);
    requestBuilder.setHeader(HEADER_USER_AGENT_PARAMETER,HEADER_USER_AGENT_VALUE);

    List<Header> headers=customHeader();
    if(null!=headers&& headers.size()>0){
      for (Header header:headers){
        requestBuilder.setHeader(header);
      }
    }

    log.debug("Send http execute: {} {}", url, method.name());
    log.debug("Payload: {}", payload);
    final Future<org.apache.http.HttpResponse> future = client.execute(requestBuilder.build(), null);
    try {
      final org.apache.http.HttpResponse response = future.get();

      InputStream inputStream=response.getEntity().getContent();
      result.setHttpStatus(response.getStatusLine().getStatusCode());
      if(HttpStatus.SC_OK ==response.getStatusLine().getStatusCode()){
        String x=IOUtils.toString(inputStream);
        result.setContent(x);

      }

    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    //结束

    return result;

  }

//  public HttpResponse execute2(String url, HttpRequestMethod method, String payload,String encoding) {
//
//
//    try {
//      URL url1 = new URL(url);
//      log.debug("Post to {}", url);
//
//      HttpURLConnection connection = null;
//      if (url.startsWith("https")) {
//        connection = (HttpsURLConnection) url1.openConnection();
//      } else {
//        connection = (HttpURLConnection) url1.openConnection();
//      }
//
//      // 发送POST请求必须设置如下两行
//      connection.setDoOutput(true);
//      connection.setDoInput(true);
//
//      connection.setRequestProperty(HEADER_CONNECTION_PARAMETER, HEADER_CONNECTION_KEEP_ALIVE_VALUE);
//      connection.setRequestProperty(HEADER_USER_AGENT_PARAMETER,HEADER_USER_AGENT_VALUE);
//
//      customConnection(connection);
//
//      connection.setRequestMethod(method.name());
//
////      connection.connect();
//
//      log.debug("Connection: {}", connection);
//      log.debug("Send http execute: {} {}", url, connection.getRequestMethod());
//      log.debug("Payload: {}", payload);
//      IOUtils.write(payload, connection.getOutputStream(), encoding);
//
//      HttpResponse response = new HttpResponse();
//      response.setHttpStatus(connection.getResponseCode());
//      if (response.getHttpStatus() == 200) {
//        List<String> lines = IOUtils.readLines(connection.getInputStream(), encoding);
//        StringBuilder sb = new StringBuilder();
//        lines.forEach(sb::append);
//        log.debug("Response Payload: {}", sb.toString());
//        response.setContent(sb.toString());
//      } else {
//        List<String> lines = IOUtils.readLines(connection.getErrorStream(), encoding);
//        StringBuilder sb = new StringBuilder();
//        lines.forEach(sb::append);
//        log.debug("Response Error Payload: {}", sb.toString());
//        response.setErrorMassage(sb.toString());
//      }
//
//      return response;
//
//    } catch (MalformedURLException e) {
//      log.debug("Invalid URL for reuqest {}",e.getMessage());
//      e.printStackTrace();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
}
