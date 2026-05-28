package com.paycoms.cp7.api.common.model;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@NoArgsConstructor // MyBatis가 객체를 생성할 때 필요함
@AllArgsConstructor // @Builder 사용 시 필요
@Builder
@Alias("Excel") // XML에서 resultType="Excel"로 쓸 수 있게 해줌
public class Excel {
    private String ceateKeyString; // 고유 키 (예: 이메일 + 타임스탬프)
    private int createKeyNo; // 고유 번호 (예: 시퀀스 번호)
    private String cell01; // 셀 값
    private String cell02; // 셀 값
    private String cell03; // 셀 값
    private String cell04; // 셀 값
    private String cell05; // 셀 값
    private String cell06; // 셀 값
    private String cell07; // 셀 값
    private String cell08; // 셀 값
    private String cell09; // 셀 값
    private String cell10; // 셀 값
    private String cell11; // 셀 값
    private String cell12; // 셀 값
    private String cell13; // 셀 값
    private String cell14; // 셀 값
    private String cell15; // 셀 값
    private String cell16; // 셀 값
    private String cell17; // 셀 값
    private String cell18; // 셀 값
    private String cell19; // 셀 값
    private String cell20; // 셀 값
    private String cell21; // 셀 값
    private String cell22; // 셀 값
    private String cell23; // 셀 값
    private String cell24; // 셀 값
    private String cell25; // 셀 값
    private String cell26; // 셀 값
    private String cell27; // 셀 값
    private String cell28; // 셀 값
    private String cell29; // 셀 값
    private String cell30; // 셀 값
    private String cell31; // 셀 값
    private String cell32; // 셀 값
    private String cell33; // 셀 값
    private String cell34; // 셀 값
    private String cell35; // 셀 값
    private String cell36; // 셀 값
    private String cell37; // 셀 값
    private String cell38; // 셀 값
    private String cell39; // 셀 값
    private String cell40; // 셀 값
    private String cell41; // 셀 값
    private String cell42; // 셀 값
    private String cell43; // 셀 값
    private String cell44; // 셀 값
    private String cell45; // 셀 값
    private String cell46; // 셀 값
    private String cell47; // 셀 값
    private String cell48; // 셀 값
    private String cell49; // 셀 값
    private String cell50; // 셀 값
}