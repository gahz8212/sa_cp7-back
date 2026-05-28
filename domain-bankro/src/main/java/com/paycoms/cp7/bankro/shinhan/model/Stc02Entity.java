package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Stc02Entity extends CommonEntity {
  private String owpsJiRqstId;
  private String owpsTrxRefNo;
  private String owpsContNo;
  private String owpsEAcno;
  private int owpsTrxBnkC;
  private String owpsTrxAcNo;
  private String trxdt;
  private String trxTime;
  private int jiamt;
  private String filler2;

  public Stc02Entity(byte[] rawData) {
    super(rawData);

    this.owpsJiRqstId = ByteParser.readString(rawData, 304, 20);
    this.owpsTrxRefNo = ByteParser.readString(rawData, 324, 20);
    this.owpsContNo = ByteParser.readString(rawData, 344, 20);
    this.owpsEAcno = ByteParser.readString(rawData, 364, 12);
    this.owpsTrxBnkC = NumberUtils.parseInt(ByteParser.readString(rawData, 376, 3), 0);
    this.owpsTrxAcNo = ByteParser.readString(rawData, 379, 20);
    this.trxdt = ByteParser.readString(rawData, 399, 8);
    this.trxTime = ByteParser.readString(rawData, 407, 6);
    this.jiamt = NumberUtils.parseInt(ByteParser.readString(rawData, 413, 18), 0);
    this.filler2 = ByteParser.readString(rawData, 431, 373);
  }
}
