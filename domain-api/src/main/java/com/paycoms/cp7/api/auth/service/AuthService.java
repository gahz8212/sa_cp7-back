package com.paycoms.cp7.api.auth.service;

import com.paycoms.cp7.api.auth.dto.LoginRequest;
import com.paycoms.cp7.api.auth.dto.ReissueRequest;
import com.paycoms.cp7.api.auth.dto.TokenResponse;
import com.paycoms.cp7.api.auth.mapper.AuthMapper;
import com.paycoms.cp7.api.auth.model.AuthEntity;
import com.paycoms.cp7.global.auth.auth.jwt.JwtProvider;
import com.paycoms.cp7.global.auth.config.RedisConfig;
import com.paycoms.cp7.global.error.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RedisConfig redisConfig;
    private final AuthMapper authMapper;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @SuppressWarnings("null")
    public TokenResponse login(LoginRequest request, HttpServletResponse response) {
        String email = request.getEmail();
        String accessToken = jwtProvider.createAccessToken(email);
        jwtProvider.createRefreshToken(response, email);

        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresIn(JwtProvider.accessTokenExpirationTime)
                .build();
    }

    public TokenResponse reissue(ReissueRequest request, String refreshToken) {
        String email = request.getEmail();

        // Redis에서 저장된 Refresh Token 조회
        String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + email);

        // 저장된 Refresh Token과 요청에서 받은 Refresh Token이 일치하는지 확인
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new BusinessException("AUTH_001", email);
        }

        // 새로운 Access Token 생성
        String newAccessToken = jwtProvider.createAccessToken(email);

        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .accessTokenExpiresIn(JwtProvider.accessTokenExpirationTime)
                .build();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtProvider.resolveToken(request);
        long expiration = jwtProvider.getExpiration(accessToken);
        redisConfig.setBlackList(accessToken, "logout", expiration);
    }

    public List<AuthEntity> test() {
        return authMapper.test();
    }
}