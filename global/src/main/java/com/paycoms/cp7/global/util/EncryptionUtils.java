package com.paycoms.cp7.global.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;

@Component
public class EncryptionUtils {
  private static final int IV_SIZE = 12; // GCM 권장
  private static final int TAG_LENGTH = 128;
  private static String base64Key;

  @Value("${encrypt.key}")
  public void setKey(String key) {
    EncryptionUtils.base64Key = key;
  }

  private static final int KEY_SIZE = 256;

  public static String generateKey() throws Exception {
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(KEY_SIZE);
    SecretKey key = keyGen.generateKey();
    System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  // 암호화
  public static String encrypt(String plainText) throws Exception {
    byte[] keyBytes = Base64.getDecoder().decode(base64Key);
    SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

    // IV 생성
    byte[] iv = new byte[IV_SIZE];
    SecureRandom random = new SecureRandom();
    random.nextBytes(iv);

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);

    cipher.init(Cipher.ENCRYPT_MODE, key, spec);
    byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

    // IV + 암호문 합치기
    byte[] result = new byte[iv.length + cipherText.length];
    System.arraycopy(iv, 0, result, 0, iv.length);
    System.arraycopy(cipherText, 0, result, iv.length, cipherText.length);

    return Base64.getEncoder().encodeToString(result);
  }

  // 복호화
  public static String decrypt(String encryptedText) throws Exception {
    byte[] decoded = Base64.getDecoder().decode(encryptedText);

    byte[] iv = new byte[IV_SIZE];
    byte[] cipherText = new byte[decoded.length - IV_SIZE];

    System.arraycopy(decoded, 0, iv, 0, IV_SIZE);
    System.arraycopy(decoded, IV_SIZE, cipherText, 0, cipherText.length);

    byte[] keyBytes = Base64.getDecoder().decode(base64Key);
    SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);

    cipher.init(Cipher.DECRYPT_MODE, key, spec);
    byte[] plainText = cipher.doFinal(cipherText);

    return new String(plainText, "UTF-8");
  }
}
