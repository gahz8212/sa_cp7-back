package com.paycoms.cp7.global.auth.auth.interceptor;

import com.paycoms.cp7.global.auth.annotation.Auth;
import com.paycoms.cp7.global.auth.annotation.AuthPolicy;
import com.paycoms.cp7.global.auth.auth.jwt.JwtProvider;
import com.paycoms.cp7.global.error.BusinessException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

  private final JwtProvider jwtProvider;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (!(handler instanceof HandlerMethod method))
      return true;

    Auth auth = method.getMethodAnnotation(Auth.class);

    if (auth != null && auth.value() == AuthPolicy.PUBLIC) {
      return true;
    }

    String token = jwtProvider.resolveToken(request);
    if (token == null || !jwtProvider.validateToken(token)) {
      throw new BusinessException("SYS_403");
    }

    if (auth != null && auth.value() == AuthPolicy.READ_WRITE) {
      if (jwtProvider.isReadOnly(token)) {
        throw new BusinessException("AUTH_002");
      }
    }

    return true;
  }
}