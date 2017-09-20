package com.minlia.iot.signature.binder;

import static com.minlia.iot.signature.XmlSignatureAnnotationHelper.*;

import com.minlia.iot.body.SignatureBody;

/**
 * Created by will on 9/20/17.
 * 抽象出几种类型的签名组合方式
 */
public class SignatureBinder {

  public static final String WECHAT_STYLE_SALT_PARAMETER_PREFIX = "key=";

  /**
   * 默认绑定
   * MD5签名方式
   * 排除SALT前缀
   * 无前缀
   * UTF-8
   * @param body
   */
  public static void bind(SignatureBody body) {
    bindSign(body.getRaw(),body.getSalt(),body.getExcludeSaltParameter(),body.getAlgorithmic(),body.getSaltParameterPrefix(),body.getCharset(),body.getCaseControl(),body.getDelimiter());
  }
}
