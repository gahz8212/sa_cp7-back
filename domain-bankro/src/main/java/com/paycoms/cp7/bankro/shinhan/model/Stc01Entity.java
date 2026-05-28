package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Stc01Entity extends CommonEntity {
  private String owpsCmsVrtlAcno;
  private String owpsEAcno;
  private int ipamt;
  private String jkyo;
  private String owpsTrxRefNo;
  private String ipMnNm;
  private String filler2;

  public Stc01Entity(byte[] rawData) {
    super(rawData);

    this.owpsCmsVrtlAcno = ByteParser.readString(rawData, 304, 16);
    this.owpsEAcno = ByteParser.readString(rawData, 320, 12);
    this.ipamt = NumberUtils.parseInt(ByteParser.readString(rawData, 332, 18), 0);
    this.jkyo = ByteParser.readString(rawData, 350, 100);
    this.owpsTrxRefNo = ByteParser.readString(rawData, 450, 20);
    this.ipMnNm = ByteParser.readString(rawData, 470, 100);
    this.filler2 = ByteParser.readString(rawData, 570, 234);
  }
}
