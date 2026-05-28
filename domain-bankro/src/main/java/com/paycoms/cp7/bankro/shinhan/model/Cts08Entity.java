package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Cts08Entity extends CommonEntity {
  private String owpsMsnoMsgSer;
  private String owpsMsnoMsgRespCval;
  private String owpsMsnoMsgRespMsgCtnt;
  private String owpsEAcno;
  private String filler2;

  public Cts08Entity(byte[] rawData) {
    super(rawData);

    this.owpsMsnoMsgSer = ByteParser.readString(rawData, 304, 14);
    this.owpsMsnoMsgRespCval = ByteParser.readString(rawData, 318, 8);
    this.owpsMsnoMsgRespMsgCtnt = ByteParser.readString(rawData, 326, 100);
    this.owpsEAcno = ByteParser.readString(rawData, 426, 12);
    this.filler2 = ByteParser.readString(rawData, 438, 366);
  }
}
