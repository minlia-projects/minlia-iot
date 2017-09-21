package com.minlia.iot.http;

import com.google.common.collect.Lists;
import java.net.HttpURLConnection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * HTTP请求发送器
 */
@Slf4j
public class FormDataWithJsonApiHttpExecutor extends AbstractApiHttpExecutor {

//	@Override
//	public void customConnection(HttpURLConnection connection) {
//		connection.setRequestProperty(HEADER_ACCEPT_PARAMETER,HEADER_ACCEPT_ALL_ACCEPTED_VALUE);
//		connection.setRequestProperty(HEADER_CONTENT_TYPE_PARAMETER, APPLICATION_FORM_DATA_VALUE_WITH_ENCODING);
//	}

	@Override
	public List<Header> customHeader() {
		List<Header> headers= Lists.newArrayList();
		headers.add(new BasicHeader(HEADER_ACCEPT_PARAMETER,HEADER_ACCEPT_ALL_ACCEPTED_VALUE));
		headers.add(new BasicHeader(HEADER_CONTENT_TYPE_PARAMETER, APPLICATION_FORM_DATA_VALUE_WITH_ENCODING));
		return headers;
	}


}