package com.paycoms.cp7.api.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelCell {
    private String value;
    private int row;
    private int col;
    private Integer rowspan;
}
