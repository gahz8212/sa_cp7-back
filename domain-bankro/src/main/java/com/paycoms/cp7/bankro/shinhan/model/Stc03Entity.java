package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Stc03Entity extends CommonEntity {
  private String stsAcno;
  private String owpsCusNm;
  private String ipDt;
  private int ipamt;
  private String owpsTrxRefNo;
  private String filler2;

  public Stc03Entity(byte[] rawData) {
    super(rawData);

    this.stsAcno = ByteParser.readString(rawData, 304, 12);
    this.owpsCusNm = ByteParser.readString(rawData, 316, 100);
    this.ipDt = ByteParser.readString(rawData, 416, 8);
    this.ipamt = NumberUtils.parseInt(ByteParser.readString(rawData, 424, 18), 0);
    this.owpsTrxRefNo = ByteParser.readString(rawData, 442, 20);
    this.filler2 = ByteParser.readString(rawData, 462, 342);
  }
}
