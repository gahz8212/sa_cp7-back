package com.paycoms.cp7.api.common.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExcelMappingTemplate {
    private Long id;
    private String templateName;
    private String headerStructure; // JSON String
    private String mappingRules;    // JSON String (TargetColumns JSON)
    private String targetSysType;   // EMPLOYEE, PAYROLL 등
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
