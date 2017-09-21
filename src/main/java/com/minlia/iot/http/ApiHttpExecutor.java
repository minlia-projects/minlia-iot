package com.minlia.iot.http;

import com.minlia.iot.requestor.HttpRequestMethod;
import java.net.HttpURLConnection;
import java.util.List;
import org.apache.http.Header;

/**
 * Created by will on 9/10/17.
 *
 *
 */
public interface ApiHttpExecutor {

  public static final String DEFAULT_ENCODING ="UTF-8";

  public static final String HEADER_ACCEPT_PARAMETER="Accept";
  public static final String HEADER_ACCEPT_ALL_ACCEPTED_VALUE="*/*";

  public static final String HEADER_CONNECTION_PARAMETER="Connection";
  public static final String HEADER_CONNECTION_KEEP_ALIVE_VALUE ="Keep-Alive";

  public static final String HEADER_USER_AGENT_PARAMETER="User-Agent";
  public static final String HEADER_USER_AGENT_VALUE="XAPI-Requestor/1.4.3.RELEASE";// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"

  public static final String HEADER_CONTENT_TYPE_PARAMETER="Content-Type";
  public static final String APPLICATION_JSON_VALUE_WITH_ENCODING="application/json;charset="+DEFAULT_ENCODING;
  public static final String APPLICATION_XML_VALUE_WITH_ENCODING="application/xml;charset="+DEFAULT_ENCODING;
  public static final String APPLICATION_FORM_DATA_VALUE_WITH_ENCODING="application/x-www-form-urlencoded;charset="+DEFAULT_ENCODING;


  HttpResponse execute(String url,HttpRequestMethod method, String raw,String encoding);

//  @Deprecated
//  void customConnection(HttpURLConnection connection);
  List<Header> customHeader();
}
