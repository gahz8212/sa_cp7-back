package com.paycoms.cp7.api.common.dto;

import lombok.Data;


@Data
public class ExcelCell {
    private String value;
    private String column;
    private String type;
    private int row;
    private int col;
    private Integer rowspan;
    private Integer colspan;
}
