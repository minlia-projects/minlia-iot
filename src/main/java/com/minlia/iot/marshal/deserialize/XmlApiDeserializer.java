package com.minlia.iot.marshal.deserialize;

import com.minlia.iot.annotation.XmlElementArray;
import com.minlia.iot.body.ApiHttpResponseBody;
import com.minlia.iot.body.response.StatefulApiResponseBody;
import com.minlia.iot.context.ApiRuntimeContext;
import com.minlia.iot.marshal.ApiMarshalWrappedBody;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import javax.xml.bind.annotation.XmlElement;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Created by will on 9/12/17.
 * 默认抽象JSON反序列化器
 */
@Slf4j
public class XmlApiDeserializer<RESPONSE extends ApiHttpResponseBody> extends
    AbstractAnnotationApiDeserializer<RESPONSE> {

  public StatefulApiResponseBody<RESPONSE> execute(ApiMarshalWrappedBody body,
      ApiRuntimeContext context) {
    try {
      Document document = DocumentHelper.parseText(body.getRaw());
      super.printXML(document);
      Element rootElement = document.getRootElement();

      Class<RESPONSE> statefulApiResponseBodyClass = (Class<RESPONSE>) body
          .getStatefulResponseBodyClass();

      StatefulApiResponseBody<RESPONSE> statefulApiResponseBody=(StatefulApiResponseBody<RESPONSE>)statefulApiResponseBodyClass.newInstance();
//      statefulApiResponseBody= makeResponseXmlBody(rootElement,statefulApiResponseBodyClass);
      for (Field field : statefulApiResponseBodyClass.getDeclaredFields()) {
        field.setAccessible(true);
        if (field.isAnnotationPresent(XmlElement.class)) {
          dealXmlElementAnnotation(rootElement, field, statefulApiResponseBody);
        }
      }

//      for (Field field : statefulApiResponseBodyClass.getDeclaredFields()) {
//        field.setAccessible(true);
//        if (field.isAnnotationPresent(XmlElement.class)) {
//          dealXmlElementAnnotation(rootElement, field, statefulApiResponseBody);
//        } else if (field.isAnnotationPresent(XmlElementArray.class)) {
//          dealXmlElementArrayAnnotation(rootElement, field, statefulApiResponseBody);
//        }
//      }


      Class<ApiHttpResponseBody> apiHttpResponseBodyClass =(Class<ApiHttpResponseBody>)body.getBusinessResponseBodyClass();
      ApiHttpResponseBody apiHttpResponseBody=  makeResponseXmlBody(rootElement,apiHttpResponseBodyClass);
      statefulApiResponseBody.setPayload((RESPONSE)apiHttpResponseBody);
      return statefulApiResponseBody;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  protected <E> E makeResponseXmlBody(Element rootElement,
      Class<E> clazz)
      throws IllegalAccessException, InstantiationException {
    E entity = clazz.newInstance();
    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.isAnnotationPresent(XmlElement.class)) {
        dealXmlElementAnnotation(rootElement, field, entity);
      } else if (field.isAnnotationPresent(XmlElementArray.class)) {
        dealXmlElementArrayAnnotation(rootElement, field, entity);
      }
    }
    return entity;
  }

  /**
   * 处理@{@link XmlElement}注解
   */
  @SuppressWarnings("unchecked")
  protected void dealXmlElementAnnotation(Element rootElement, Field field, Object entity) {
    XmlElement xmlElementAnnotation = field.getAnnotation(XmlElement.class);
    Element element = rootElement.element(xmlElementAnnotation.name());
    if (Objects.nonNull(element)) {
      Optional<Object> valueOptional = super
          .elementValue(rootElement, field.getType(), xmlElementAnnotation.name());
      valueOptional.ifPresent(value -> {
        try {
          field.set(entity, value);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      });
    }
  }

  /**
   * 处理@{@link XmlElementArray}注解
   */
  protected void dealXmlElementArrayAnnotation(Element rootElement, Field field, Object entity) {
    Class<?> clazz = entity.getClass();
    XmlElementArray xmlElementArrayAnnotation = field.getAnnotation(XmlElementArray.class);
    // 1. 获取总数值
    Optional<Object> indexOptional = super
        .elementValue(rootElement, Integer.class, xmlElementArrayAnnotation.indexElement());
    indexOptional.ifPresent(indeX -> {
      final int index = (Integer) indeX;
      // 2.获取数组类型
      Class<?> fieldType = field.getType();
      if (fieldType.isArray()) {
        // 获取数组元素类型
        Class<?> componentType = fieldType.getComponentType();
        // 3.生成数组对象
        Object[] objs = (Object[]) Array.newInstance(fieldType, index);
        for (int i = 0; i < index; i++) {
          try {
            final int j = i;
            // 生成每个元素对象
            objs[j] = componentType.newInstance();
            // 遍历字段
            Arrays.stream(componentType.getDeclaredFields())
                .filter(
                    componentTypeField -> componentTypeField.isAnnotationPresent(XmlElement.class))
                .forEach(componentTypeField -> {
                  XmlElement componentTypeXmlElement = componentTypeField
                      .getAnnotation(XmlElement.class);
                  // 取得每个数组元素字段的值
                  Optional<Object> componentTypeFieldValueOptional = super
                      .elementArrayValue(rootElement, componentTypeField.getType(),
                          componentTypeXmlElement, j);
                  componentTypeFieldValueOptional.ifPresent(componentTypeFieldValue -> {
                    try {
                      componentTypeField.set(objs[j], componentTypeFieldValue);
                    } catch (IllegalAccessException e) {
                      e.printStackTrace();
                    }
                  });
                });
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        try {
          field.set(entity, objs);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    });
  }

}
