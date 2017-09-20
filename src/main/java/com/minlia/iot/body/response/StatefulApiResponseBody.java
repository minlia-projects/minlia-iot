package com.minlia.iot.body.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minlia.cloud.body.Body;

/**
 * Created by will on 9/10/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface StatefulApiResponseBody<RESPONSE> extends Body {

  Boolean isSuccess();

  RESPONSE getPayload();

  void setPayload(RESPONSE t);


}
