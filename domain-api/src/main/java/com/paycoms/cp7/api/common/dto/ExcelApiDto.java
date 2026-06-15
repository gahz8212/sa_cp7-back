package com.paycoms.cp7.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import com.paycoms.cp7.api.common.model.Excel;

public class ExcelApiDto {

    @Getter @Setter
    @Schema(description = "엑셀 업로드 요청 DTO")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UploadRequest {
        @Schema(description = "파일정보", required = true, nullable = false)
        @NotNull(message = "VALI_001.file")
        private MultipartFile file;

        @Schema(description = "시트 번호 (default: 0)", defaultValue = "0", example = "0")
        private int sheetNo = 0;

        @Schema(description = "행 번호 (default: 1)", defaultValue = "0", example = "0")
        private int rowNo = 0;

        @Schema(description = "페이지 번호 (default: 1)", defaultValue = "1", example = "1")
        private int page = 1;
    }

    @Getter @Setter
    @Schema(name = "UploadExcelResponse", description = "엑셀 업로드 응답 DTO")
    public static class UploadResponse {
        @Schema(description = "엑셀 업로드 키", example = "test@example.com-1234567890")
        private String uploadExcelKey;
        private List<Excel> dataList;
        private int totalCount;
        @Schema(description = "추론된 대상 시스템 타입 (예: EMPLOYEE, PAYROLL)")
        private String targetSysType;
        @Schema(description = "파일 이름 기반으로 판별된 백엔드 타겟 컬럼 메타데이터")
        private List<SysMetadata> targetColumns;
        @Schema(description = "저장된 헤더 구조 데이터 (Header/Data/Etc 영역 정보)")
        private Object headerStructure;
    }

    @Getter @Setter @NoArgsConstructor
    public static class StructureRequest {
        @NotEmpty(message = "헤더 구조 데이터가 비어있습니다.")
        private List<ExcelCell> flattenedHeaders;

        @NotEmpty(message = "샘플 데이터 구조가 비어있습니다.")
        private List<ExcelCell> flattenedData;

        private List<ExcelCell> flattenedEtc;
        private String fileName;
    }

    @Getter @Setter
    public static class UpdateRequest {
        @NotEmpty(message = "수정된 데이터 내역이 비어있습니다.")
        private List<ModifiedRow> modifiedRows;
        private String fileId;
    }

    @Data
    public static class SaveDataAndTemplateRequest {
        private List<ModifiedRow> modifiedRows;
        private TemplateDto template;

        @Data
        public static class TemplateDto {
            private String targetSysType;
            private String fileName;
            private Map<String, Object> headerStructure;
            private List<SysMetadata> targetColumns;
        }
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class SysMetadata {
        private String backColumn;
        private String name;
        private String description;
        private boolean required;
        private String frontColumn;
        private Integer excelColIndex;
    }

    @Getter @Setter
    public static class ModifiedRow {
        private Integer rowIndex;
        private Map<String, Object> original;
        private Map<String, Object> modified;
    }

    @Data
    public static class ExcelCell {
        private String value;
        private String column;
        private String type;
        private int row;
        private int col;
        private Integer rowspan;
        private Integer colspan;
    }

    @Getter @Setter
    @Schema(description = "엑셀 다운로드 요청 DTO")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DownloadRequest {
        @NotBlank(message = "VALI_001.fileName")
        @Schema(description = "파일명 (default: excel.xlsx)", defaultValue = "excel.xlsx", example = "excel.xlsx")
        private String fileName = "excel.xlsx";

        @NotBlank(message = "VALI_001.sheetName")
        @Schema(description = "Sheet명 (default: 거래내역)", defaultValue = "Sheet1", example = "Sheet1")
        private String sheetName = "Sheet1";

        @NotBlank(message = "VALI_001.passwordYn")
        @Schema(description = "비밀번호 설정 여부 (default: N)", defaultValue = "N", example = "N")
        private String passwordYn = "N";

        @Schema(description = "비밀번호(8자이상 / 대문자,소문자,숫자,특수문자 중 3개 조합)", example = "password1234!@")
        private String password;

        @Schema(description = "필드명")
        private String[] fields;

        @NotNull(message = "VALI_001.datas")
        @Schema(description = "데이터")
        private List<Object[]> datas;
    }
}