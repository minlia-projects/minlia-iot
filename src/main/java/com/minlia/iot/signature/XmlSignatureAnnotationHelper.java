package com.minlia.iot.signature;

import com.minlia.iot.annotation.ApiRequestEntity;
import com.minlia.iot.annotation.Signature;
import com.minlia.iot.context.ApiRuntimeContext;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.util.Assert;

/**
 * 使用XML注解进行签名处理
 *
 * Created by will on 3/24/17.
 *
 * @author will
 * @author json
 * @author garen
 * @since 1.0.0.RELEASE
 */
@Slf4j
public final class XmlSignatureAnnotationHelper {

  /**
   * 默认使用MD5方式
   * 转换为大写
   * &分隔
   * 不使用盐值参数添加
   * 无盐值参数
   * UTF-8
   *
   * @param salt partnerApiKey
   */
//  public static void bindSign(Object entity, String salt) {
//    Assert.nonNull(entity);
//    Assert.nonBlank(salt, "Salt should not be null");
//    bindSign(entity, salt, Boolean.TRUE, SignatureAlgorithmic.MD5, "", DEFAULT_CHARSET,
//        CaseControl.TO_UPPER_CASE, DEFAULT_DELIMITER);
//  }
  public static boolean prepareCheckSign(String xml, String salt, Class<?> clazz) {
    return prepareCheckSign(xml, salt, Boolean.TRUE, clazz, SignatureAlgorithmic.MD5, "",
        ApiRuntimeContext.DEFAULT_CHARSET, CaseControl.TO_UPPER_CASE, ApiRuntimeContext.DEFAULT_DELIMITER);
  }

  /**
   * 预检验签名
   *
   * @param xml xml字符串
   * @param salt partnerApiKey
   * @param clazz 类型
   * @return 是否通过验证
   */
  @SuppressWarnings("unchecked")
  public static boolean prepareCheckSign(String xml, String salt, Boolean excludeSaltParameter,
      Class<?> clazz,
      SignatureAlgorithmic algorithmic, String saltParameterPrefix, String charset,
      CaseControl caseControl, String delimiter) {
    Field signField = signField(clazz);
    XmlElement xmlElementAnnotation = signField.getAnnotation(XmlElement.class);
    try {
      Document document = DocumentHelper.parseText(xml);
      Element rootElement = document.getRootElement();
      List<FieldPaired> fieldPaireds = new LinkedList<>();
      String targetSign = null;
      for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
        Element element = iterator.next();
        if (element.getName().equals(xmlElementAnnotation.name())) {
          targetSign = element.getTextTrim();
        } else {
          String text = element.getTextTrim();
          if (StringUtils.isNotBlank(text)) {
            fieldPaireds.add(new FieldPaired(element.getName(), text));
          }
        }
      }
      if (StringUtils.isBlank(targetSign)) {
        log.warn("Sign shoud not be empty.");
      }
      String signStr = "";

      signStr = makeSignBySinpleFieldList(fieldPaireds, salt, excludeSaltParameter, algorithmic,
          saltParameterPrefix, charset, caseControl, delimiter);
      return signStr.equals(targetSign);
    } catch (DocumentException e) {
      e.printStackTrace();
      return false;
    }
  }


  /**
   * 检查签名
   *
   * @param entity 实体对象
   * @param salt partnerApiKey
   * @param targetSign 目标签名字符串
   * @return 是否通过验证
   */
  public static boolean checkSign(Object entity, String salt, String targetSign) {
    return checkSign(entity, salt, targetSign, Boolean.TRUE, SignatureAlgorithmic.MD5, "",
        ApiRuntimeContext.DEFAULT_CHARSET, CaseControl.TO_UPPER_CASE, ApiRuntimeContext.DEFAULT_DELIMITER);
  }

  public static boolean checkSign(Object entity, String salt, String targetSign,
      Boolean excludeSaltParameter, SignatureAlgorithmic algorithmic, String saltParameterPrefix,
      String charset, CaseControl caseControl, String delimiter) {
    String sign = generateSign(entity, salt, excludeSaltParameter, algorithmic, saltParameterPrefix,
        charset, caseControl, delimiter);
    log.info(sign);
    return sign.equals(targetSign);
  }


  /**
   * @param entity 待处理的实体对象
   * @param salt 签名的盐值  即加密串
   * @param excludeSaltParameter 是否排除掉盐值参数
   * @param algorithmic 签名算法 加密的类型
   * @param saltParameterPrefix 盐值前缀
   * @param charset 字符集
   */
  public static void bindSign(Object entity, String salt, Boolean excludeSaltParameter,
      SignatureAlgorithmic algorithmic, String saltParameterPrefix, String charset,
      CaseControl caseControl, String delimiter) {

    Assert.notNull(entity);
    Assert.notNull(salt, "Salt should not be null");

    if (StringUtils.isEmpty(charset)) {
      charset = ApiRuntimeContext.DEFAULT_CHARSET;
    }

    if (!excludeSaltParameter) {
      //检查saltParameterPrefix是否不为空
      if (StringUtils.isEmpty(saltParameterPrefix)) {
        throw new RuntimeException("已指定excludeSaltParameter为false, 必须指定saltParameterPrefix参数");
      }
    }
    String signStr = generateSign(entity, salt, excludeSaltParameter, algorithmic,
        saltParameterPrefix,
        charset, caseControl, delimiter);
    Class<?> clazz = entity.getClass();
    try {
      Field signField = signField(clazz);
      signField.setAccessible(true);
      signField.set(entity, signStr);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 生成签名
   *
   * @param entity 实体对象
   * @param salt partnerApiKey
   */
  public static String generateSign(Object entity, String salt, Boolean excludeSaltParameter,
      SignatureAlgorithmic algorithmic, String saltParameterPrefix, String charset,
      CaseControl caseControl, String delimiter) {
    List<FieldPaired> fieldPaireds = new LinkedList<>();
    Field signField = signField(entity.getClass());
    dealSinpleEntity(entity, fieldPaireds, signField, algorithmic, saltParameterPrefix, charset);
    return makeSignBySinpleFieldList(fieldPaireds, salt, excludeSaltParameter, algorithmic,
        saltParameterPrefix, charset, caseControl, delimiter);
  }

  /**
   * 根据SinpleField列表生成签名
   *
   * 加2个参数   delimiter,caseConvert
   *
   * @param fieldPaireds SinpleField的列表
   * @param salt partnerApiKey
   * @return 生成的签名字符串
   */
  private static String makeSignBySinpleFieldList(List<FieldPaired> fieldPaireds, String salt,
      Boolean excludeKeyParameter, SignatureAlgorithmic algorithmic, String saltParameterPrefix,
      String charset, CaseControl caseControl, String delimiter) {
    List<String> list = fieldPaireds.stream()
        .sorted(new AsciiSortedComparator<>(FieldPaired::getProperty)).map(
            FieldPaired::toString).collect(Collectors.toList());

    //在对象上添加特殊属性, 当不排除时添加
    if (!excludeKeyParameter) {
      if (StringUtils.isEmpty(saltParameterPrefix)) {
        throw new RuntimeException("指定了需要添加KEY=到salt前面, 却没有指定前前缀, 请检查官方文档,再做相应调整");
      }
      list.add(saltParameterPrefix + salt);
    }

    // 未加密字符串
    String unencrypted = "";
    try {
      unencrypted = new String(String.join(delimiter, list).getBytes(), charset);
      //将salt添加到最后面
      if (!StringUtils.isEmpty(salt)) {
        if (excludeKeyParameter) {
          unencrypted += salt;
        }
      }
      log.debug("Unencrypted String is: {}", unencrypted);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String result = "";
    switch (algorithmic) {
      case MD2:
        result = DigestUtils.md2Hex(unencrypted);
        break;
      case MD5:
        result = DigestUtils.md5Hex(unencrypted);
        break;
      case SHA1:
        result = DigestUtils.sha1Hex(unencrypted);
        break;
      case SHA256:
        result = DigestUtils.sha256Hex(unencrypted);
        break;
      case SHA384:
        result = DigestUtils.sha384Hex(unencrypted);
        break;
      case SHA512:
        result = DigestUtils.sha512Hex(unencrypted);
        break;
      default:
        throw new RuntimeException("不支持的签名类型");
    }

    if (null != caseControl) {
      switch (caseControl) {
        case TO_LOWER_CASE:
          result = result.toLowerCase();
          break;
        case TO_UPPER_CASE:
          result = result.toUpperCase();
          break;
      }
    }

    log.debug("Encrypted Signature is: {}", result);
    return result;

  }

  /**
   * 处理单个实体
   *
   * @param entity 实体对象
   * @param list SinpleField的列表
   * @param signField 签名字段
   */
  private static void dealSinpleEntity(Object entity, List<FieldPaired> list, Field signField,
      SignatureAlgorithmic algorithmic, String saltParameterPrefix, String charset) {
    Field[] fields = ArrayUtils.addAll(entity.getClass().getSuperclass().getDeclaredFields(),
        entity.getClass().getDeclaredFields());
    for (Field field : fields) {
      if (field.equals(signField)) {
        continue;
      }
      field.setAccessible(true);
      try {
        if (field.isAnnotationPresent(XmlElement.class)) {
          Object value = field.get(entity);
          if (value == null) {
            continue;
          }
          list.add(new FieldPaired(field.getAnnotation(XmlElement.class).name(), value));
        } else if (field.isAnnotationPresent(ApiRequestEntity.class)) {
          Object nextEntity = field.get(entity);
          dealSinpleEntity(nextEntity, list, signField, algorithmic, saltParameterPrefix, charset);
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 查询签名字段
   *
   * @param clazz 实体类
   * @return 签名字段
   */
  private static Field signField(Class<?> clazz) {
    String signFieldName = clazz.getAnnotation(Signature.class).value();
    String[] floors = signFieldName.split("\\.");
    Field field = null;
    Class<?> tempClass = clazz;
    for (int i = 0; i < floors.length; i++) {
      try {
        field = tempClass.getDeclaredField(floors[i]);
        tempClass = field.getType();
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      }
    }
    return field;
  }

  public static void setFieldValue(Object obj, String fieldName, Object value) {
    Field field = getAccessibleField(obj, fieldName);
    if (field == null) {
      throw new IllegalArgumentException(
          "Could not find field [" + fieldName + "] on target [" + obj + "]");
    } else {
      try {
        field.set(obj, value);
      } catch (IllegalAccessException var5) {
        log.error("不可能抛出的异常:{}", var5.getMessage());
      }

    }
  }


  public static Object getFieldValue(Object obj, String fieldName) {
    if (obj instanceof Map) {
      return ((Map) obj).get(fieldName);
    } else {
      Field field = getAccessibleField(obj, fieldName);
      if (field == null) {
        throw new IllegalArgumentException(
            "Could not find field [" + fieldName + "] on target [" + obj + "]");
      } else {
        Object result = null;

        try {
          result = field.get(obj);
        } catch (IllegalAccessException var5) {
          log.error("不可能抛出的异常{}", var5.getMessage());
        }

        return result;
      }
    }
  }

  public static Field getAccessibleField(Object obj, String fieldName) {
    return getAccessibleField(obj.getClass(), fieldName);
  }

  public static Field getAccessibleField(Class<?> cls, String fieldName) {
    Validate.notNull(cls, "class could not be null", new Object[0]);
    Validate.notBlank(fieldName, "property could not be null", new Object[0]);
    Class superClass = cls;

    while (superClass != Object.class) {
      try {
        Field field = superClass.getDeclaredField(fieldName);
        makeAccessible(field);
        return field;
      } catch (NoSuchFieldException var4) {
        superClass = superClass.getSuperclass();
      }
    }

    return null;
  }

  public static void makeAccessible(Field field) {
    if ((!Modifier.isPublic(field.getModifiers()) || !Modifier
        .isPublic(field.getDeclaringClass().getModifiers()) || Modifier
        .isFinal(field.getModifiers())) && !field.isAccessible()) {
      field.setAccessible(true);
    }
  }
}
