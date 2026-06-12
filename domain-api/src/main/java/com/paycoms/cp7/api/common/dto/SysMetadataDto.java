package com.paycoms.cp7.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysMetadataDto {
    private String backColumn;
    private String name;
    private String description;
    private boolean required;
    private String frontColumn;
    private Integer excelColIndex;
}
