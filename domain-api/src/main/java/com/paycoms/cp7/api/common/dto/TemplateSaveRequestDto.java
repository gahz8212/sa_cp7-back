package com.paycoms.cp7.api.common.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TemplateSaveRequestDto {
    private String targetSysType; // "EMPLOYEE", "PAYROLL" 등
    private String templateName;  // "우리회사 급여대장 양식"
    
    // 프론트엔드에서 만든 매핑 정보
    private List<UserMappingDto> userMapping; 
    
    // 프론트엔드에서 구성한 그리드 헤더 구조 (JSON 문자열 또는 Map으로 받음)
    private Map<String, Object> headerStructure; 

    @Data
    public static class UserMappingDto {
        private String frontColumn; // 엑셀의 컬럼명 (예: "고객명")
        private String backColumn;  // 백엔드 메타데이터의 필드명 (예: "memberName")
    }
}
