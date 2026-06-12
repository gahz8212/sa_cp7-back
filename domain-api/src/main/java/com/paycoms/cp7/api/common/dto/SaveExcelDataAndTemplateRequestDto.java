package com.paycoms.cp7.api.common.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SaveExcelDataAndTemplateRequestDto {
    private List<ModifiedRow> modifiedRows;
    private TemplateDto template;

    @Data
    public static class TemplateDto {
        private String targetSysType;
        private String fileName;
        private Map<String, Object> headerStructure;
        private List<SysMetadataDto> targetColumns;
    }
}
