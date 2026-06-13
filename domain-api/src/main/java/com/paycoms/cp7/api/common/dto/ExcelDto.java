package com.paycoms.cp7.api.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class ExcelDto {
    private int no; // 고유 번호 (예: 시퀀스 번호)
    private Map<String, Object> excelData;
}
