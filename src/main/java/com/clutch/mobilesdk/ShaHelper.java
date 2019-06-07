package com.clutch.mobilesdk;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper for HMAC/SHA logic.
 */
public class ShaHelper {

  private static final String HMAC_SHA512 = "HmacSHA512";

  /**
   * Characters to use in the hexadecimal alphabet.
   */
  private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  /**
   * Encode a byte array to a hex string.
   * @param data
   * @return
   */
  protected static String encodeHex(byte[] data) {
    char[] chars = new char[data.length * 2];
    int pos = 0;
    for(int i = 0; i < data.length; i++) {
      chars[pos++] = HEX_CHARS[(data[i] & 0xF0) >>> 4];
      chars[pos++] = HEX_CHARS[data[i] & 0x0F];
    }
    return new String(chars);
  }

  /**
   * Create a SHA512 hash from a string.
   * @param input Input string
   * @return SHA512 hash
   */
  public static String sha512(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      byte[] messageDigest = md.digest(input.getBytes());
      return encodeHex(messageDigest);
    } catch(NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Create an HMAC-SHA512 signature/hash.
   * @param input Input to sign
   * @param key Key to use for signing
   * @return HMAC-SHA512 signature/hash
   */
  public static String hmacSha512(String input, String key) {
    try {
      SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
      Mac mac = Mac.getInstance(HMAC_SHA512);
      mac.init(secretKeySpec);
      return encodeHex(mac.doFinal(input.getBytes()));
    } catch(NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    } catch(InvalidKeyException ex) {
      throw new RuntimeException(ex);
    }
  }

}
