package com.minlia.iot.signature.verification;

import com.minlia.iot.body.SignatureVerificationBody;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by will on 9/10/17.
 */
@Slf4j
public class DefaultXmlSignatureVerificationProcessor implements SignatureVerificationProcessor {


  @Override
  public Boolean verify(SignatureVerificationBody body) {
    return Boolean.TRUE;
  }


}
