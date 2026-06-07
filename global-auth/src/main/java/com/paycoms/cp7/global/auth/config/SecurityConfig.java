package com.paycoms.cp7.global.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.paycoms.cp7.global.auth.auth.jwt.CustomAccessDeniedHandler;
import com.paycoms.cp7.global.auth.auth.jwt.CustomAuthenticationEntryPoint;
import com.paycoms.cp7.global.auth.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomAccessDeniedHandler accessDeniedHandler;
        private final CustomAuthenticationEntryPoint authenticationEntryPoint;
        private final JwtAuthenticationFilter jwtAuthenticationFilter; // 주입!

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable()) // API 서버이므로 CSRF 비활성화
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 미사용
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/v3/api-docs/**",
                                                                "/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/swagger-resources/**",
                                                                "/webjars/**",
                                                                // 아래처럼 /api가 붙은 경로도 명시적으로 함께 추가합니다.
                                                                "/api/v3/api-docs/**",
                                                                "/api/api-docs/**",
                                                                "/api/swagger-ui/**",
                                                                "/api/swagger-ui.html",
                                                                "/api/swagger-resources/**",
                                                                "/api/webjars/**"
                                                                // ,"/common/upload-excel/**"
                                                        )
                                                .permitAll()
                                                .anyRequest().permitAll() // 그 외 모든 요청은 토큰 필수!
                                )
                                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 끼워넣기
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler)
                                                .authenticationEntryPoint(authenticationEntryPoint));

                return http.build();
        }
}