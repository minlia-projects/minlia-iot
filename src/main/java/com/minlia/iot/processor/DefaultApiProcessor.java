package com.minlia.iot.processor;

import com.minlia.iot.body.ApiHttpRequestBody;
import com.minlia.iot.context.ApiRuntimeContext;
import com.minlia.iot.body.ApiHttpResponseBody;

/**
 * Created by will on 9/12/17.
 */
public class DefaultApiProcessor<REQUEST extends ApiHttpRequestBody, RESPONSE extends ApiHttpResponseBody> extends
    AbstractAnnotationApiProcessor<REQUEST, RESPONSE> {

  public DefaultApiProcessor(ApiRuntimeContext apiRuntimeContext) {
    super(apiRuntimeContext);
  }

}
