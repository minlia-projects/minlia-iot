package com.minlia.iot.marshal.serialize;

import com.minlia.iot.scope.HttpMediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by will on 9/10/17.
 */
@Slf4j
public class JsonApiSerializer<T> extends AbstractAnnotationApiSerializer<T> {

  @Override
  public String serialize(T body, HttpMediaType type) {
    return serializeAsJson(body);
  }

}
