package com.minlia.iot.signature.verification;

import com.minlia.iot.body.SignatureVerificationBody;

/**
 * Created by will on 9/10/17.
 * 签名处理器
 */
public interface SignatureVerificationProcessor {

  /**
   * 校验签名是否正确
   */
  public Boolean verify(SignatureVerificationBody body);

}
