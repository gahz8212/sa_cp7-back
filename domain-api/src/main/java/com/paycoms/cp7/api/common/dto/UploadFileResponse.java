package com.paycoms.cp7.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UploadFileResponse", description = "파일 업로드 응답 DTO")
public class UploadFileResponse {
    @Schema(description = "원본 파일명", example = "test.xlsx")
    private String orgFileName;

    @Schema(description = "서버 저장 파일명", example = "123e4567-e89b-12d3-a456-426614174000.xlsx")
    private String saveFileName;
}