package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.global.util.NumberUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommonEntity {
  private int header;

  // 이미지 명세 기준 필드 선언
  private String owpsUpmuC; // 업무CODE (4)
  private String owpsMfrC; // 업체CODE (3)
  private String owpsMsgC; // 전문CODE (5)
  private String owpsMsgSer; // 전문일련번호 (14)
  private String msgTwayG; // 전문송수신구분 (1)
  private String owpsOrgC; // 기관CODE (3)
  private String owpsRespCval; // 응답코드 (8)
  private String owpsRespMsgCtnt; // 응답메시지 (100)
  private String sndDt; // 전송일자 (8)
  private String sndTime; // 전송시각 (6)
  private String etcCtnt; // 기타 (100)
  private String filler1; // filler1 (48)

  public CommonEntity(byte[] rawData) {
    // Offset 계산 기준: 이미지의 '누적' 값에서 '길이'를 뺀 지점이 시작점입니다.
    // 전문 헤더 4바이트가 이미 포함된 rawData라고 가정 시, 누적 바이트 위치를 그대로 따라갑니다.
    this.header = NumberUtils.parseInt(ByteParser.readString(rawData, 0, 4), 0);
    this.owpsUpmuC = ByteParser.readString(rawData, 4, 4);
    this.owpsMfrC = ByteParser.readString(rawData, 8, 3);
    this.owpsMsgC = ByteParser.readString(rawData, 11, 5);
    this.owpsMsgSer = ByteParser.readString(rawData, 16, 14);
    this.msgTwayG = ByteParser.readString(rawData, 30, 1);
    this.owpsOrgC = ByteParser.readString(rawData, 31, 3);
    this.owpsRespCval = ByteParser.readString(rawData, 34, 8);
    this.owpsRespMsgCtnt = ByteParser.readString(rawData, 42, 100);
    this.sndDt = ByteParser.readString(rawData, 142, 8);
    this.sndTime = ByteParser.readString(rawData, 150, 6);
    this.etcCtnt = ByteParser.readString(rawData, 156, 100);
    this.filler1 = ByteParser.readString(rawData, 256, 48); // 누적 300
  }
}