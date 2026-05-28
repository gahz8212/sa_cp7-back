package com.paycoms.cp7.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "로그인 응답 DTO")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    @Schema(description = "인증 타입", example = "Bearer")
    private String grantType; // 보통 "Bearer" 고정
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken; // 실제 인증 토큰
    @Schema(description = "액세스 토큰 만료 시간 (ms)", example = "3600000")
    private Long accessTokenExpiresIn; // 만료 시간 (ms)

    /**
     * 단순하게 토큰만 전달하는 정적 생성 메서드 (편의용)
     */
    public static TokenResponse of(String accessToken) {
        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }
}