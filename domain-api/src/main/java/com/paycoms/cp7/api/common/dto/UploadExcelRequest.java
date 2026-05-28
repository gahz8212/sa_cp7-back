package com.paycoms.cp7.api.common.dto;

import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "엑셀 업로드 요청 DTO")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (보안 및 지연로딩 대비)
public class UploadExcelRequest {
    @SuppressWarnings("deprecation")
    @Schema(description = "파일정보", required = true, nullable = false)
    @NotNull(message = "VALI_001.file")
    private MultipartFile file;

    @Schema(description = "시트 번호 (default: 0)", defaultValue = "0", example = "0")
    private int sheetNo = 0;

    @Schema(description = "행 번호 (default: 1)", defaultValue = "1", example = "1")
    private int rowNo = 1;
}