package com.minlia.iot.http;

import java.net.HttpURLConnection;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP请求发送器
 */
@Slf4j
public class FormDataWithJsonApiHttpExecutor extends AbstractApiHttpExecutor {

	@Override
	public void customConnection(HttpURLConnection connection) {
		connection.setRequestProperty(HEADER_ACCEPT_PARAMETER,HEADER_ACCEPT_ALL_ACCEPTED_VALUE);
		connection.setRequestProperty(HEADER_CONTENT_TYPE_PARAMETER, APPLICATION_FORM_DATA_VALUE_WITH_ENCODING);
	}



}