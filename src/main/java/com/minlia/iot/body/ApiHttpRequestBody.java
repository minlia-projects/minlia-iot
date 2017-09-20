package com.minlia.iot.body;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.minlia.cloud.body.Body;

@JsonInclude(value = Include.NON_NULL)
public class ApiHttpRequestBody implements Body {

}
