package com.paycoms.cp7.global.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "표준 응답 DTO")
public class ApiResponse<T> {
    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;
    @Schema(description = "응답 코드", example = "SYS_200")
    private String code;
    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;
    @Schema(description = "응답 데이터")
    private T data;
}