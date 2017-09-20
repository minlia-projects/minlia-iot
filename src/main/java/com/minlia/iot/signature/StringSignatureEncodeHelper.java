package com.minlia.iot.signature;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by will on 9/10/17.
 */
@Slf4j
public class StringSignatureEncodeHelper {


  public static String sign(String content, String keyValue, String charset)
      throws UnsupportedEncodingException, Exception {
    return urlEncoder(encrypt(content, keyValue, charset), charset);
  }

  public static String urlEncoder(String str, String charset) throws UnsupportedEncodingException {
    String result = URLEncoder.encode(str, charset);
    return result;
  }
  public static String urlDecoder(String str, String charset) throws UnsupportedEncodingException {
    String result = URLDecoder.decode(str, charset);
    return result;
  }

  public static String encrypt(String content, String keyValue, String charset)
      throws UnsupportedEncodingException, Exception {
    if (keyValue != null) {
      return base64(md5(content + keyValue, charset), charset);
    }
    return base64(md5(content, charset), charset);
  }

  public static String base64(String str, String charset) throws UnsupportedEncodingException {
    String encoded = base64Encode(str.getBytes(charset));
    return encoded;
  }

  public static String md5(String str, String charset) throws Exception {
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(str.getBytes(charset));
    byte[] result = md.digest();
    StringBuffer sb = new StringBuffer(32);
    for (int i = 0; i < result.length; i++) {
      int val = result[i] & 0xff;
      if (val <= 0xf) {
        sb.append("0");
      }
      sb.append(Integer.toHexString(val));
    }
    return sb.toString().toLowerCase();
  }

  private static char[] base64EncodeChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
      'J', 'K', 'L',
      'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e',
      'f', 'g',
      'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
      '0', '1',
      '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

  public static String base64Encode(byte[] data) {
    StringBuffer sb = new StringBuffer();
    int len = data.length;
    int i = 0;
    int b1, b2, b3;
    while (i < len) {
      b1 = data[i++] & 0xff;
      if (i == len) {
        sb.append(base64EncodeChars[b1 >>> 2]);
        sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
        sb.append("==");
        break;
      }
      b2 = data[i++] & 0xff;
      if (i == len) {
        sb.append(base64EncodeChars[b1 >>> 2]);
        sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
        sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
        sb.append("=");
        break;
      }
      b3 = data[i++] & 0xff;
      sb.append(base64EncodeChars[b1 >>> 2]);
      sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
      sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
      sb.append(base64EncodeChars[b3 & 0x3f]);
    }
    return sb.toString();
  }


}
