package com.paycoms.cp7.global.auth.auth;

import com.paycoms.cp7.global.auth.annotation.LoginUser;
import com.paycoms.cp7.global.auth.auth.jwt.JwtProvider;
import com.paycoms.cp7.global.auth.common.UserInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

  private final JwtProvider jwtProvider;

  @SuppressWarnings("null")
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    // 파라미터에 @LoginUser가 붙어 있는지 검사
    return parameter.hasParameterAnnotation(LoginUser.class);
  }

  @SuppressWarnings("null")
  @Override
  public UserInfoDto resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    // Request에서 토큰 꺼내기
    HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
    String token = request.getHeader("Authorization");

    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
      // 토큰에서 유저 정보(ID 등) 추출하여 반환 (유저 객체 자체를 넘겨도 됨)
      return jwtProvider.getUserInfo(token);
    }
    return null;
  }
}