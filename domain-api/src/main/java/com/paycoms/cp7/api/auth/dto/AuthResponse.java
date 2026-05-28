package com.paycoms.cp7.api.auth.dto;

import lombok.Builder;
import com.paycoms.cp7.api.auth.model.AuthEntity;

@Builder
public record AuthResponse(
        String id,
        String email,
        String name,
        String joinedAt // 가입일 등 필요한 필드 추가
) {
    // Entity(Model)를 DTO로 변환하는 정적 팩토리 메서드
    public static AuthResponse from(AuthEntity auth) {
        return AuthResponse.builder()
                .id(auth.getId())
                .email(auth.getEmail())
                .name(auth.getName())
                // .joinedAt(auth.getCreatedAt().toString()) // 예시
                .build();
    }
}