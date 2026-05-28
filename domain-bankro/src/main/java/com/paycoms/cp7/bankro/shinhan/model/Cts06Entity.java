package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Cts06Entity extends CommonEntity {
  private String owpsEAcno;
  private int owpsEAcS;
  private String owpsContNo;
  private int owpsContS;
  private String owpsCusNm;
  private int acJan;
  private int intAmt;
  private String filler2;

  public Cts06Entity(byte[] rawData) {
    super(rawData);

    this.owpsEAcno = ByteParser.readString(rawData, 304, 12);
    this.owpsEAcS = NumberUtils.parseInt(ByteParser.readString(rawData, 316, 2), 0);
    this.owpsContNo = ByteParser.readString(rawData, 318, 20);
    this.owpsContS = NumberUtils.parseInt(ByteParser.readString(rawData, 338, 2), 0);
    this.owpsCusNm = ByteParser.readString(rawData, 340, 100);
    this.acJan = NumberUtils.parseInt(ByteParser.readString(rawData, 440, 18), 0);
    this.intAmt = NumberUtils.parseInt(ByteParser.readString(rawData, 458, 18), 0);
    this.filler2 = ByteParser.readString(rawData, 476, 328);
  }
}
