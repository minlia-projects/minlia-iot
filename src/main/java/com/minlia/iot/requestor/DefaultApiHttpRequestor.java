package com.minlia.iot.requestor;

import com.minlia.iot.body.ApiHttpRequestBody;
import com.minlia.iot.body.response.StatefulApiResponseBody;
import com.minlia.iot.processor.ApiProcessor;
import com.minlia.iot.body.ApiHttpResponseBody;

/**
 * Created by Administrator on 2016/9/6.
 */
public class DefaultApiHttpRequestor<REQUEST extends ApiHttpRequestBody, RESPONSE extends ApiHttpResponseBody> implements
    ApiHttpRequestor<REQUEST, RESPONSE> {

    protected ApiProcessor<REQUEST, RESPONSE> apiProcessor;

    public ApiProcessor<REQUEST, RESPONSE> getApiProcessor() {
        return apiProcessor;
    }

    public void setApiProcessor(
        ApiProcessor<REQUEST, RESPONSE> apiProcessor) {
        this.apiProcessor = apiProcessor;
    }

    public DefaultApiHttpRequestor(ApiProcessor<REQUEST, RESPONSE> apiProcessor) {
        this.apiProcessor = apiProcessor;
    }

    /**
     * 组装请求参数完成后调用处理器进行请求处理
     * @param body
     * @return
     */
    @Override
    public StatefulApiResponseBody<RESPONSE> request(REQUEST body) {
        return apiProcessor.process(body);
    }
}
