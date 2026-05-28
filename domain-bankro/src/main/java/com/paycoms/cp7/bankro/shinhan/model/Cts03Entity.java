package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Cts03Entity extends CommonEntity {
  private int owpsTrxBnkC;
  private String owpsTrxAcNo;
  private String owpsTrxDepOwnrNm;
  private int owpsAcS;
  private String filler2;

  public Cts03Entity(byte[] rawData) {
    super(rawData);

    this.owpsTrxBnkC = NumberUtils.parseInt(ByteParser.readString(rawData, 304, 3), 0);
    this.owpsTrxAcNo = ByteParser.readString(rawData, 307, 20);
    this.owpsTrxDepOwnrNm = ByteParser.readString(rawData, 327, 100);
    this.owpsAcS = NumberUtils.parseInt(ByteParser.readString(rawData, 427, 2), 0);
    this.filler2 = ByteParser.readString(rawData, 429, 375);
  }
}
