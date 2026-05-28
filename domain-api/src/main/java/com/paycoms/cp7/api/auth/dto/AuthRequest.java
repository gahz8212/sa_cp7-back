package com.paycoms.cp7.api.auth.dto;

import com.paycoms.cp7.api.auth.model.AuthEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthRequest {
    private String id;
    private String name;
    private String email;

    public AuthEntity toEntity() {
        return AuthEntity.builder()
                .id(this.id)
                .name(this.name) // 실무에선 암호화 필수!
                .email(this.email)
                .build();
    }
}