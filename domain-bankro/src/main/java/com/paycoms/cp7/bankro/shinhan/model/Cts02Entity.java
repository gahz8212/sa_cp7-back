package com.paycoms.cp7.bankro.shinhan.model;

import com.paycoms.cp7.bankro.shinhan.common.ByteParser;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Cts02Entity extends CommonEntity {
    // 개별부 (누적 301~500)
    private String owpsCmsVrtlAcno; // e계좌번호 (16)
    private String owpsContNo; // 계약번호 (20)
    private String owpsUseEndDt; // 사용종료일자 (8)
    private String owpsEAcno; // e코드 (12)
    private String filler2; // filler2 (444)

    public Cts02Entity(byte[] rawData) {
        super(rawData); // CommonEntity의 생성자에서 공통부를 파싱합니다.

        // 개별부 파싱 (300 바이트 지점부터 시작)
        this.owpsCmsVrtlAcno = ByteParser.readString(rawData, 304, 16);
        this.owpsContNo = ByteParser.readString(rawData, 320, 20);
        this.owpsUseEndDt = ByteParser.readString(rawData, 340, 8);
        this.owpsEAcno = ByteParser.readString(rawData, 348, 12);
        this.filler2 = ByteParser.readString(rawData, 360, 444);
    }
}