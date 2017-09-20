package com.minlia.iot.requestor;

import com.minlia.iot.body.ApiHttpRequestBody;
import com.minlia.iot.body.response.StatefulApiResponseBody;
import com.minlia.iot.body.ApiHttpResponseBody;

public interface ApiHttpRequestor<REQUEST extends ApiHttpRequestBody, RESPONSE extends ApiHttpResponseBody> {

    StatefulApiResponseBody<RESPONSE> request(REQUEST body);
}
