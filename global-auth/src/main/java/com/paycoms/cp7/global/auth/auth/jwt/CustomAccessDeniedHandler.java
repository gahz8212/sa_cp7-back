package com.paycoms.cp7.global.auth.auth.jwt;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paycoms.cp7.global.common.ApiResponse;
import com.paycoms.cp7.global.util.MessageUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final MessageUtils messageUtils;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        // 403 오류 응답 생성 (예: 코드 "SYS_403")
        ApiResponse<Object> apiResponse = messageUtils.createResponse("SYS_403", null);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
