package com.paycoms.cp7.bankro.shinhan.common;

import java.nio.charset.Charset;

public class ByteParser {
  // EUC-KR 캐릭터셋 상수로 정의
  private static final Charset EUC_KR = Charset.forName("EUC-KR");

  /**
   * 바이트 배열을 지정된 구간만큼 잘라서 문자열로 반환
   * 
   * @param src    원본 바이트 배열
   * @param offset 시작 위치
   * @param length 자를 길이
   * @return Trim 처리된 문자열
   */
  public static String readString(byte[] src, int offset, int length) {
    if (src == null || src.length < offset + length) {
      return ""; // 배열 범위를 벗어날 경우 빈 문자열 반환 (방어 코드)
    }

    byte[] dest = new byte[length];
    System.arraycopy(src, offset, dest, 0, length);

    // EUC-KR로 변환하고 양 끝 공백(Padding)을 제거해서 반환
    return new String(dest, EUC_KR).trim();
  }

  public static String readOwpsMsgC(byte[] src) {
    return readString(src, 11, 5);
  }

  public static boolean validation(byte[] src) {
    if (ByteParser.readString(src, 4, 4).equals("OWPS") && ByteParser.readString(src, 8, 3).equals("PCS"))
      return true;

    return false;
  }
}