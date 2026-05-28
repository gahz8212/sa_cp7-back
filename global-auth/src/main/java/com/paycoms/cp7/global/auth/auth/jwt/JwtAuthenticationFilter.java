package com.paycoms.cp7.global.auth.auth.jwt;

import com.paycoms.cp7.global.auth.config.RedisConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.paycoms.cp7.global.auth.annotation.Auth;
import com.paycoms.cp7.global.auth.annotation.AuthPolicy;
import com.paycoms.cp7.global.common.ApiResponse;
import com.paycoms.cp7.global.util.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisConfig redisConfig;
    private final JwtProvider jwtProvider;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ObjectMapper objectMapper;
    private final MessageUtils messageUtils;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            // 현재 요청에 매핑된 컨트롤러 메서드 정보 가져오기
            HandlerExecutionChain executionChain = requestMappingHandlerMapping.getHandler(request);

            if (executionChain != null && executionChain.getHandler() instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) executionChain.getHandler();

                // @Auth(AuthPolicy.PUBLIC) 체크
                Auth authAnnotation = handlerMethod.getMethodAnnotation(Auth.class);
                if (authAnnotation != null && authAnnotation.value() == AuthPolicy.PUBLIC) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        } catch (Exception e) {
            logger.error("Could not determine handler for request", e);
        }

        // 헤더에서 토큰 추출 (Authorization: Bearer <token>)
        String token = jwtProvider.resolveToken(request);

        // 토큰 유효성 검사
        if (token != null && jwtProvider.validateToken(token) && !redisConfig.hasKeyBlackList(token)) {
            // 유효하면 사용자 정보를 시큐리티 컨텍스트에 담음 (인증 성공)
            Authentication auth = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response); // 다음 필터로 진행
        } else {
            ApiResponse<Object> apiResponse = messageUtils.createResponse("SYS_403", null);

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        }
    }

    @SuppressWarnings("null")
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.contains("/v3/api-docs") ||
                path.contains("/api-docs") ||
                path.contains("/swagger-ui") ||
                path.contains("/swagger-resources") ||
                path.contains("/webjars");
    }
}