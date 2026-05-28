package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Cts04Entity extends CommonEntity {
  private String owpsJiRqstId;
  private String owpsHrnkEAcno;
  private String owpsEAcno;
  private int owpsTrxBnkC;
  private String owpsTrxAcNo;
  private String owpsTrxDepOwnrNm;
  private String owpsRcptG;
  private String jkyo;
  private int jiamt;
  private int owpsContLevG;
  private int owpsCusG;
  private String owpsCusNm;
  private int rsdtG;
  private String rsdcarNatC;
  private int silnoG;
  private String silno;
  private String owpsTrxRefNo;
  private String jidt;
  private int jiG;
  private int acJan;
  private String filler2;

  public Cts04Entity(byte[] rawData) {
    super(rawData);

    this.owpsJiRqstId = ByteParser.readString(rawData, 304, 20);
    this.owpsHrnkEAcno = ByteParser.readString(rawData, 324, 12);
    this.owpsEAcno = ByteParser.readString(rawData, 336, 12);
    this.owpsTrxBnkC = NumberUtils.parseInt(ByteParser.readString(rawData, 348, 3), 0);
    this.owpsTrxAcNo = ByteParser.readString(rawData, 351, 20);
    this.owpsTrxDepOwnrNm = ByteParser.readString(rawData, 371, 100);
    this.owpsRcptG = ByteParser.readString(rawData, 471, 1);
    this.jkyo = ByteParser.readString(rawData, 472, 100);
    this.jiamt = NumberUtils.parseInt(ByteParser.readString(rawData, 572, 18), 0);
    this.owpsContLevG = NumberUtils.parseInt(ByteParser.readString(rawData, 590, 1), 0);
    this.owpsCusG = NumberUtils.parseInt(ByteParser.readString(rawData, 591, 2), 0);
    this.owpsCusNm = ByteParser.readString(rawData, 593, 100);
    this.rsdtG = NumberUtils.parseInt(ByteParser.readString(rawData, 693, 2), 0);
    this.rsdcarNatC = ByteParser.readString(rawData, 695, 2);
    this.silnoG = NumberUtils.parseInt(ByteParser.readString(rawData, 697, 2), 0);
    this.silno = ByteParser.readString(rawData, 699, 30);
    this.owpsTrxRefNo = ByteParser.readString(rawData, 729, 20);
    this.jidt = ByteParser.readString(rawData, 749, 8);
    this.jiG = NumberUtils.parseInt(ByteParser.readString(rawData, 757, 2), 0);
    this.acJan = NumberUtils.parseInt(ByteParser.readString(rawData, 759, 18), 0);
    this.filler2 = ByteParser.readString(rawData, 777, 27);
  }
}
