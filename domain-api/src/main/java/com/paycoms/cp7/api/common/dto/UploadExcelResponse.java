package com.paycoms.cp7.api.common.dto;

import com.paycoms.cp7.api.common.model.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Schema(name = "UploadExcelResponse", description = "엑셀 업로드 DTO")
public class UploadExcelResponse {
    @Schema(description = "엑셀 업로드 키", example = "test@example.com-1234567890")
    private String uploadExcelKey;
    
    private List<Excel> dataList;
    private int totalCount;
    
    @Schema(description = "추론된 대상 시스템 타입 (예: EMPLOYEE, PAYROLL)")
    private String targetSysType;
    
    @Schema(description = "파일 이름 기반으로 판별된 백엔드 타겟 컬럼 메타데이터")
    private List<SysMetadataDto> targetColumns;
    
    @Schema(description = "저장된 헤더 구조 데이터 (Header/Data/Etc 영역 정보)")
    private Object headerStructure;
}