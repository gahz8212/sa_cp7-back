package com.paycoms.cp7.global.auth.config;

import com.paycoms.cp7.global.auth.auth.LoginUserArgumentResolver;
import com.paycoms.cp7.global.auth.auth.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final LoginUserArgumentResolver loginUserArgumentResolver;
  private final AuthInterceptor authInterceptor;

  @SuppressWarnings("null")
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(loginUserArgumentResolver);
  }

  @SuppressWarnings("null")
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor)
        .addPathPatterns("/**"); // 적용할 경로 지정
  }
}