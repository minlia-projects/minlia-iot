package com.minlia.iot.marshal.deserialize;

import com.minlia.iot.body.response.StatefulApiResponseBody;
import com.minlia.iot.context.ApiRuntimeContext;
import com.minlia.iot.marshal.ApiMarshalWrappedBody;

/**
 * 反序列化器
 */
public interface ApiDeserializer<RESPONSE> {

  StatefulApiResponseBody<RESPONSE> deserialize(ApiMarshalWrappedBody body,ApiRuntimeContext context);

}
