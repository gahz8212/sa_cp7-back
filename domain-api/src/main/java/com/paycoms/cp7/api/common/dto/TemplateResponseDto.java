package com.paycoms.cp7.api.common.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class TemplateResponseDto {
    private Long templateId;
    private String templateName;
    private String targetSysType;
    private List<TemplateSaveRequestDto.UserMappingDto> userMapping;
    private Map<String, Object> headerStructure;
    private LocalDateTime createdAt;
}
