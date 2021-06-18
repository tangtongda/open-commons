package com.tangtongda.open.commons.utils;

import java.util.Random;
import java.util.UUID;

/**
 * {@link IDUtil} uuId generator util
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2020/10/19
 */
public abstract class IDUtil {
  private IDUtil() {
    throw new InstantiationError("Must not instantiate this class");
  }

  public static final Integer ID_LENGTH = 18;
  private static final String TO_BASE_64_URL =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

  /**
   * get Long type uuId
   *
   * @return 18 length Long uuId
   */
  public static Long longId() {
    StringBuilder randomStr = new StringBuilder(String.valueOf(System.currentTimeMillis()));
    randomStr.append(new Random().nextInt(99999));
    while (randomStr.length() < ID_LENGTH) {
      randomStr.append("0");
    }
    return Long.parseLong(randomStr.toString());
  }

  /**
   * package jdk UUID to 22 length string
   *
   * @return 22 length UUID
   */
  public static String uuid() {
    UUID uuid = UUID.randomUUID();
    return base64Encode(uuid);
  }

  /**
   * base64 encode
   *
   * @param uuid uuid
   * @return id
   */
  private static String base64Encode(UUID uuid) {
    char[] chs = new char[22];
    long most = uuid.getMostSignificantBits();
    long least = uuid.getLeastSignificantBits();
    int k = chs.length - 1;
    for (int i = 0; i < 10; i++, least >>>= 6) {
      chs[k--] = TO_BASE_64_URL.charAt((int) (least & 0x3f));
    }
    chs[k--] = TO_BASE_64_URL.charAt((int) ((least & 0x3f) | ((most & 0x03) << 4)));
    most >>>= 2;
    for (int i = 0; i < 10; i++, most >>>= 6) {
      chs[k--] = TO_BASE_64_URL.charAt((int) (most & 0x3f));
    }
    chs[k] = TO_BASE_64_URL.charAt((int) most);
    return new String(chs);
  }

  /**
   * replace '-' in uuId
   *
   * @param uuId uuid string
   * @return base64Encode string
   */
  private static String base64Encode(String uuId) {
    StringBuilder sb = new StringBuilder(uuId);
    sb.insert(20, '-');
    sb.insert(16, '-');
    sb.insert(12, '-');
    sb.insert(8, '-');
    UUID uuid = UUID.fromString(sb.toString());
    return base64Encode(uuid);
  }

  /**
   * reset to 32 length uuId
   *
   * @param base64 base64 string
   * @return id
   */
  private static String base64Decode(String base64) {
    StringBuilder sb = new StringBuilder();
    for (int i = base64.length(); i > 0; i -= 2) {
      int c1 = TO_BASE_64_URL.indexOf(String.valueOf(base64.charAt(i - 1)));
      int c2 = TO_BASE_64_URL.indexOf(String.valueOf(base64.charAt(i - 2)));
      sb.insert(0, Integer.toHexString(c1 & 0xf));
      sb.insert(0, Integer.toHexString((c1 >>> 4 & 0x3) | (c2 << 2 & 0xc)));
      if (i != 2) {
        sb.insert(0, Integer.toHexString(c2 >>> 2 & 0xf));
      }
    }
    return sb.toString();
  }
}
