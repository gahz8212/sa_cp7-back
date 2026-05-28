package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Cts01Entity extends CommonEntity {
  private String owpsCmsVrtlAcno; // e계좌번호 (16)
  private String owpsUseSttDt; // 사용시작일자 (8)
  private int owpsContFixYn; // 계약확정여부 (1) - NUMBER
  private String owpsContG; // 계약구분 (2)
  private String owpsAcOwnG; // 계좌소유구분 (1)
  private String owpsContNo; // 계약번호 (20)
  private String owpsHrnkContNo; // 상위계약번호 (20)
  private String owpsThnkContNo; // 최상위계약번호 (20)
  private String owpsOrdrProvrNo; // 발주사업자번호 (10)
  private int owpsContLevG; // 계약레벨구분 (1) - NUMBER

  private int owpsCusG; // 고객구분 (2) - NUMBER
  private String owpsCusNm; // 고객명 (100)
  private int rsdtG; // 거주자구분 (2) - NUMBER
  private String rsdcarNatC; // 거주지국가CODE (2)
  private int silnoG; // 실명번호구분 (2) - NUMBER
  private String silno; // 실명번호 (30)
  private String owpsEAcno; // e코드 (12)
  private String filler2; // filler2 (251)

  public Cts01Entity(byte[] rawData) {
    super(rawData); // CommonEntity의 생성자에서 공통부를 파싱합니다.

    this.owpsCmsVrtlAcno = ByteParser.readString(rawData, 304, 16);
    this.owpsUseSttDt = ByteParser.readString(rawData, 320, 8);
    this.owpsContFixYn = NumberUtils.parseInt(ByteParser.readString(rawData, 328, 1), 0);
    this.owpsContG = ByteParser.readString(rawData, 329, 2);
    this.owpsAcOwnG = ByteParser.readString(rawData, 331, 1);
    this.owpsContNo = ByteParser.readString(rawData, 332, 20);
    this.owpsHrnkContNo = ByteParser.readString(rawData, 352, 20);
    this.owpsThnkContNo = ByteParser.readString(rawData, 372, 20);
    this.owpsOrdrProvrNo = ByteParser.readString(rawData, 376, 10);
    this.owpsContLevG = NumberUtils.parseInt(ByteParser.readString(rawData, 402, 1), 0); // 누적 399 (이미지상 99로 표기된 부분)

    this.owpsCusG = NumberUtils.parseInt(ByteParser.readString(rawData, 403, 2), 0);
    this.owpsCusNm = ByteParser.readString(rawData, 405, 100);
    this.rsdtG = NumberUtils.parseInt(ByteParser.readString(rawData, 505, 2), 0);
    this.rsdcarNatC = ByteParser.readString(rawData, 507, 2);
    this.silnoG = NumberUtils.parseInt(ByteParser.readString(rawData, 509, 2), 0);
    this.silno = ByteParser.readString(rawData, 511, 30);
    this.owpsEAcno = ByteParser.readString(rawData, 541, 12);
    this.filler2 = ByteParser.readString(rawData, 553, 251); // 총 804바이트 (4+800 혹은 헤더포함 계산 필요)
  }
}