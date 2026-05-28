package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Hck02Entity extends CommonEntity {
  private int owpsS;
  private String msgRcvdt;
  private String msgRcvtime;
  private String msgSnddt;
  private String msgSndTime;
  private String filler2;

  public Hck02Entity(byte[] rawData) {
    super(rawData);

    this.owpsS = NumberUtils.parseInt(ByteParser.readString(rawData, 304, 1), 0);
    this.msgRcvdt = ByteParser.readString(rawData, 305, 8);
    this.msgRcvtime = ByteParser.readString(rawData, 313, 6);
    this.msgSnddt = ByteParser.readString(rawData, 319, 8);
    this.msgSndTime = ByteParser.readString(rawData, 327, 6);
    this.filler2 = ByteParser.readString(rawData, 333, 471);
  }
}
