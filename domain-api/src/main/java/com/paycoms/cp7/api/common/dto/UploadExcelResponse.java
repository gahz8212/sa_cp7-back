package com.paycoms.cp7.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UploadExcelResponse", description = "엑셀 업로드 DTO")
public class UploadExcelResponse {
    @Schema(description = "엑셀 업로드 키", example = "test@example.com-1234567890")
    private String uploadExcelKey;
}