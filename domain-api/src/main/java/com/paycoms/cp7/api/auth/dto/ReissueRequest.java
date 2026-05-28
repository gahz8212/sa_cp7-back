package com.paycoms.cp7.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "토큰 재발행 요청 DTO")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (보안 및 지연로딩 대비)
public class ReissueRequest {
    @SuppressWarnings("deprecation")
    @Schema(description = "사용자 메일주소", example = "test@test.com", required = true, nullable = false, maxLength = 5, minLength = 100)
    @NotBlank(message = "VALI_001.email")
    @Email(message = "VALI_002.email")
    private String email;
}