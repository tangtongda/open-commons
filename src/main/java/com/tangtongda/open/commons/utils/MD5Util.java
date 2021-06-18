package com.tangtongda.open.commons.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5Util
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2020/10/19
 */
public class MD5Util {

  private MD5Util() {}

  /**
   * get the md5 string of param
   *
   * @param str input string
   * @return md5 secretF
   */
  public static String getMD5(String str) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(str.getBytes());
      byte[] resultByteArray = md.digest();
      return byteArrayToHex(resultByteArray).toLowerCase();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * byte array to hex
   *
   * @param byteArray bytes
   * @return new string
   */
  public static String byteArrayToHex(byte[] byteArray) {
    char[] hexDigits = {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    char[] resultCharArray = new char[byteArray.length * 2];
    int index = 0;
    for (byte b : byteArray) {
      resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
      resultCharArray[index++] = hexDigits[b & 0xf];
    }
    return new String(resultCharArray);
  }
}
