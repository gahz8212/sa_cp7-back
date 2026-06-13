package com.paycoms.cp7.api.common.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "엑셀 다운로드 요청 DTO")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (보안 및 지연로딩 대비)
public class DownloadExcelRequest {
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