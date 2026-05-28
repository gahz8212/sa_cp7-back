package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Stc04Entity extends CommonEntity {
  private String owpsJiRqstId;
  private String owpsContNo;
  private String owpsHrnkEAcno;
  private String owpsEAcno;
  private int owpsTrxBnkC;
  private String owpsTrxAcNo;
  private String owpsTrxDepOwnrNm;
  private String owpsRcptG;
  private String jkyo;
  private String owpsCusNm;
  private String owpsTrxRefNo;
  private String trxdt;
  private String trxTime;
  private int jiamt;
  private int acJan;
  private String filler2;

  public Stc04Entity(byte[] rawData) {
    super(rawData);

    this.owpsJiRqstId = ByteParser.readString(rawData, 304, 20);
    this.owpsContNo = ByteParser.readString(rawData, 324, 20);
    this.owpsHrnkEAcno = ByteParser.readString(rawData, 344, 12);
    this.owpsEAcno = ByteParser.readString(rawData, 356, 12);
    this.owpsTrxBnkC = NumberUtils.parseInt(ByteParser.readString(rawData, 368, 3), 0);
    this.owpsTrxAcNo = ByteParser.readString(rawData, 371, 20);
    this.owpsTrxDepOwnrNm = ByteParser.readString(rawData, 391, 100);
    this.owpsRcptG = ByteParser.readString(rawData, 491, 1);
    this.jkyo = ByteParser.readString(rawData, 492, 100);
    this.owpsCusNm = ByteParser.readString(rawData, 592, 100);
    this.owpsTrxRefNo = ByteParser.readString(rawData, 692, 20);
    this.trxdt = ByteParser.readString(rawData, 712, 8);
    this.trxTime = ByteParser.readString(rawData, 720, 6);
    this.jiamt = NumberUtils.parseInt(ByteParser.readString(rawData, 726, 18), 0);
    this.acJan = NumberUtils.parseInt(ByteParser.readString(rawData, 744, 18), 0);
    this.filler2 = ByteParser.readString(rawData, 762, 42);
  }
}
