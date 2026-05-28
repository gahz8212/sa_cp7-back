package com.paycoms.cp7.api.auth.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paycoms.cp7.api.auth.dto.LoginRequest;
import com.paycoms.cp7.api.auth.dto.ReissueRequest;
import com.paycoms.cp7.api.auth.dto.TokenResponse;
import com.paycoms.cp7.api.auth.model.AuthEntity;
import com.paycoms.cp7.api.auth.service.AuthService;
import com.paycoms.cp7.global.auth.annotation.Auth;
import com.paycoms.cp7.global.auth.annotation.AuthPolicy;
import com.paycoms.cp7.global.auth.annotation.LoginUser;
import com.paycoms.cp7.global.common.ApiResponse;
import com.paycoms.cp7.global.auth.common.UserInfoDto;
import com.paycoms.cp7.global.util.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth Controller", description = "사용자 인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  private final MessageUtils messageUtils;

  @Auth(AuthPolicy.PUBLIC)
  @Operation(summary = "로그인", description = "회원의 인증 키를 발급합니다.")
  @PostMapping("/login")
  public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
    TokenResponse result = authService.login(request, response);
    return messageUtils.createResponse("SYS_200", result);
  }

  @Auth(AuthPolicy.PUBLIC)
  @Operation(summary = "토큰 재발행", description = "회원의 인증 키를 재발행합니다.")
  @PostMapping("/reissue")
  public ApiResponse<TokenResponse> reissue(@Valid @RequestBody ReissueRequest request,
      @CookieValue(value = "refreshToken", required = false) String refreshToken) {
    TokenResponse response = authService.reissue(request, refreshToken);
    return messageUtils.createResponse("SYS_200", response);
  }

  @Operation(summary = "로그아웃", description = "회원의 인증 키를 삭제합니다.")
  @GetMapping("/logout")
  public ApiResponse<Object> logout(HttpServletRequest request, HttpServletResponse response) {
    authService.logout(request, response);
    return messageUtils.createResponse("SYS_200", null);
  }

  @GetMapping("/test")
  public ResponseEntity<ApiResponse<Object>> test(@LoginUser UserInfoDto userInfo) {
    List<AuthEntity> list = authService.test();

    // 성공 코드 SYS_200과 데이터를 전달 (자동으로 다국어 처리됨)
    ApiResponse<Object> response = messageUtils.createResponse("SYS_200", list);

    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
