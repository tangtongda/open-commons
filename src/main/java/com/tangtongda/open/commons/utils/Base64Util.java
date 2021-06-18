package com.tangtongda.open.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * {@link Base64Util} Base64Util
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2020/12/11
 */
public class Base64Util {

  private static final Logger LOGGER = LoggerFactory.getLogger(Base64Util.class);

  private Base64Util() {}

  /**
   * 加密
   *
   * @param str param
   * @return encrypt
   */
  public static String encode(String str) {
    byte[] b = str.getBytes(StandardCharsets.UTF_8);
    return Base64.getEncoder().encodeToString(b);
  }

  /**
   * decode
   *
   * @param s encrypt param
   * @return string
   */
  public static String decode(String s) {
    byte[] b = null;
    String result = EMPTY;
    if (isNotBlank(s)) {
      Decoder decoder = Base64.getDecoder();
      try {
        b = decoder.decode(s);
        result = new String(b, StandardCharsets.UTF_8);
      } catch (Exception e) {
        LOGGER.error("base64 string decode failed");
        return result;
      }
    }
    return result;
  }

  /**
   * decode image string
   *
   * @param imgStr image base64 string
   * @return inputStream bytes
   */
  public static byte[] decodeImg(String imgStr) {
    byte[] b = null;
    if (!isNotBlank(imgStr)) {
      return b;
    }
    try {
      Decoder decoder = Base64.getDecoder();
      b = decoder.decode(imgStr);
      for (int i = 0; i < b.length; i++) {
        if (b[i] < 0) {
          b[i] += 256;
        }
      }
    } catch (Exception e) {
      LOGGER.error("base64 decode error", e);
    }
    return b;
  }
}
