package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Cts07Entity extends CommonEntity {
  private String owpsJiRqstId;
  private String stsAcno;
  private String owpsCmsVrtlAcno;
  private int jiamt;
  private String owpsContNo;
  private String owpsCusNm;
  private String owpsTrxRefNo;
  private String jidt;
  private String filler2;

  public Cts07Entity(byte[] rawData) {
    super(rawData);

    this.owpsJiRqstId = ByteParser.readString(rawData, 304, 20);
    this.stsAcno = ByteParser.readString(rawData, 324, 12);
    this.owpsCmsVrtlAcno = ByteParser.readString(rawData, 336, 16);
    this.jiamt = NumberUtils.parseInt(ByteParser.readString(rawData, 352, 18), 0);
    this.owpsContNo = ByteParser.readString(rawData, 370, 20);
    this.owpsCusNm = ByteParser.readString(rawData, 390, 100);
    this.owpsTrxRefNo = ByteParser.readString(rawData, 490, 20);
    this.jidt = ByteParser.readString(rawData, 510, 8);
    this.filler2 = ByteParser.readString(rawData, 518, 286);
  }
}
