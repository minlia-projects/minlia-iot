package com.minlia.iot.marshal.deserialize;

import com.minlia.cloud.marshall.JsonHelper;
import com.minlia.iot.body.response.StatefulApiResponseBody;
import com.minlia.iot.context.ApiRuntimeContext;
import com.minlia.iot.body.ApiHttpResponseBody;
import com.minlia.iot.marshal.ApiMarshalWrappedBody;

/**
 * Created by will on 9/12/17.
 * 默认抽象JSON反序列化器
 */
public class JsonApiDeserializer<T extends ApiHttpResponseBody> extends AbstractAnnotationApiDeserializer<T> {

  public StatefulApiResponseBody<T> execute(ApiMarshalWrappedBody body,ApiRuntimeContext context) {
    StatefulApiResponseBody statefulApiResponseBody = (StatefulApiResponseBody) JsonHelper
        .deserialize(body.getRaw(), body.getStatefulResponseBodyClass());
    ApiHttpResponseBody apiHttpResponseBody = (ApiHttpResponseBody) JsonHelper
        .deserialize(body.getRaw(), body.getBusinessResponseBodyClass());

    statefulApiResponseBody.setPayload(apiHttpResponseBody);

    return statefulApiResponseBody;
  }

}
