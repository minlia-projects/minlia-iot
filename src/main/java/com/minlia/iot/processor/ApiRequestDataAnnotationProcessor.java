package com.minlia.iot.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minlia.iot.signature.StringSignatureEncodeHelper;
import com.minlia.iot.annotation.ApiRequestDataTransfer;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by will on 9/11/17.
 */
@Slf4j
public class ApiRequestDataAnnotationProcessor<T> implements DataProcessor<T> {

  private static ObjectMapper objectMapper = new ObjectMapper();


  public <T> T process(T body) {
    Class<?> clazz = body.getClass();
    ApiRequestDataTransfer apiRequestDataTransfer = clazz.getAnnotation(ApiRequestDataTransfer.class);
    log.debug("ApiRequestDataTransfer from class {}", apiRequestDataTransfer);

    String sourceProperty = apiRequestDataTransfer.source();
    String targetProperty = apiRequestDataTransfer.target();
    String typeProperty = apiRequestDataTransfer.type();

    //将取到的source值 按type指定方法加密后设置给指定的target
    log.debug("ApiRequestDataTransfer from class {}", sourceProperty);

    try {
      Field sourceField = clazz.getDeclaredField(sourceProperty);
      sourceField.setAccessible(true);
      Object value = sourceField.get(body);

      String sourceValue = objectMapper.writeValueAsString(value);
      sourceValue = StringSignatureEncodeHelper.urlEncoder(sourceValue, "UTF-8");

      //设置到target对象的值
      Field targetField = clazz.getDeclaredField(targetProperty);
      targetField.setAccessible(true);
      targetField.set(body, sourceValue);
      log.debug("SourceField value {}", value);
      log.debug("SourceField sourceValue {}", sourceValue);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return body;

  }

}
