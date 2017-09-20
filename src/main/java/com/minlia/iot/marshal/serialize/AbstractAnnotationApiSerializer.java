package com.minlia.iot.marshal.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minlia.iot.processor.AbstractApiComponent;
import com.minlia.iot.scope.HttpMediaType;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.util.Assert;

/**
 * 基于注解的序列化器
 */
@Slf4j
public abstract class AbstractAnnotationApiSerializer<T> extends AbstractApiComponent implements
    ApiSerializer<T> {

  protected ObjectMapper jsonMapper = new ObjectMapper();

  /**
   * 当通用需求无法满足时需要使用自定义的方法进行
   */
  @Override
  public String serialize(T body, HttpMediaType type) {
    String result = "";
    switch (type) {
      case Json:
        result = serializeAsJson(body);
        break;

      case Xml:
        result = serializeAsXml(body);
        break;

      default:
        break;
    }
    return result;
  }


  public String serializeAsJson(T body) {
    String result = "";
    try {
      result = jsonMapper.writeValueAsString(body);
      log.debug("With post {}", result);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }

  public String serializeAsXml(T body) {
    Class<?> clazz = body.getClass();
    Element rootElement = DocumentHelper
        .createElement(clazz.getAnnotation(XmlRootElement.class).name());
    Document document = DocumentHelper.createDocument(rootElement);
    checkRequestPropertyValidAndAddElement(rootElement, body);
    printXML(document);
    return rootElement.asXML();
  }

  /**
   * 检查请求参数有效性并加入节点
   */
  private void checkRequestPropertyValidAndAddElement(Element rootElement, final T body) {
    Class<?> clazz = body.getClass();
    Arrays.stream(
        ArrayUtils.addAll(clazz.getSuperclass().getDeclaredFields(), clazz.getDeclaredFields()))
        .filter(field -> field.isAnnotationPresent(XmlElement.class))
        .forEach(field -> {
          XmlElement xmlElementAnnotation = field.getAnnotation(XmlElement.class);
          try {
            field.setAccessible(true);
            Object value = field.get(body);
            addElement(rootElement, xmlElementAnnotation.name(), value,
                xmlElementAnnotation.required());
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        });
  }

  /**
   * 添加节点
   */
  private void addElement(Element rootElement, String elementName, Object obj, boolean required) {
    if (obj == null && !required) {
      return;
    }
    Assert.notNull(obj, elementName);
    Class<?> clazz = obj.getClass();
    Element element = rootElement.addElement(elementName);
    if (clazz == Integer.class || clazz == Long.class) {
      element.setText(obj.toString());
    } else {
      element.addCDATA(obj.toString());
    }
  }
}
