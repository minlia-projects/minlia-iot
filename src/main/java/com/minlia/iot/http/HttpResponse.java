package com.minlia.iot.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * HTTP响应体
 * 
 * @author qiuzhenhao
 *
 */
@Getter
@Setter
@ToString
public class HttpResponse {

	private int httpStatus;

	private String errorMassage;

	private String content;

	public boolean isOK() {
		return httpStatus == 200;
	}
}
