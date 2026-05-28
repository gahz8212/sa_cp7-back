package com.paycoms.cp7.global.auth.auth.jwt;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.paycoms.cp7.global.auth.common.UserInfoDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Component
public class JwtProvider {
  private final SecretKey key;
  public static long accessTokenExpirationTime;
  public static long refreshTokenExpirationTime;
  private final RedisTemplate<String, String> redisTemplate;

  public JwtProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-expiration-time}") long accessTokenExpirationTime,
      @Value("${jwt.refresh-token-expiration-time}") long refreshTokenExpirationTime,
      RedisTemplate<String, String> redisTemplate) {
    // 문자열 시크릿 키를 SecretKey 객체로 변환
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    this.redisTemplate = redisTemplate;
    JwtProvider.accessTokenExpirationTime = accessTokenExpirationTime;
    JwtProvider.refreshTokenExpirationTime = refreshTokenExpirationTime;
  }

  public String createAccessToken(String email) {
    return createToken(email, accessTokenExpirationTime);
  }

  @SuppressWarnings("null")
  public void createRefreshToken(HttpServletResponse response, String email) {
    String refreshToken = createToken(email, refreshTokenExpirationTime);

    // 쿠키에 refreshToken 저장 (HTTPOnly, Secure 옵션 설정)
    Cookie cookie = new Cookie("refreshToken", refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // HTTPS 환경에서만 전송
    cookie.setPath("/api/auth/reissue"); // 쿠키가 유효한 경로 설정
    cookie.setMaxAge((int) (JwtProvider.refreshTokenExpirationTime / 1000)); // 만료 시간 설정
    response.addCookie(cookie);

    // Redis에 저장: Key(email) - Value(refreshToken)
    // 만료 시간 설정 (단위: MILLISECONDS)
    redisTemplate.opsForValue().set(
        "RT:" + email,
        refreshToken,
        JwtProvider.refreshTokenExpirationTime,
        TimeUnit.MILLISECONDS);
  }

  public String createToken(String email, long expireTime) {
    Date now = new Date();
    return Jwts.builder()
        .subject(email)
        .claim("name", "그랩투비")
        .claim("readonly", true)
        .claim("serviceType", "cp")
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expireTime))
        .signWith(key)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // 토큰에서 이메일(Subject) 추출
  public String getEmail(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
  }

  public Authentication getAuthentication(String token) {
    String email = getEmail(token);
    // 실제 서비스에서는 DB에서 유저 정보를 조회하여 UserDetails를 생성하는 것이 좋습니다.
    UserDetails userDetails = User.builder()
        .username(email)
        .password("") // 비밀번호는 이미 토큰으로 검증됨
        .authorities("ROLE_USER")
        .build();
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  // 토큰에서 정보추출
  public UserInfoDto getUserInfo(String token) {
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    UserInfoDto userInfo = new UserInfoDto();
    userInfo.setId(claims.getSubject());
    userInfo.setName(claims.get("name", String.class));
    userInfo.setServiceType(claims.get("serviceType", String.class));
    userInfo.setReadOnly(claims.get("readonly", Boolean.class));
    return userInfo;
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  /**
   * 토큰의 남은 유효시간(ms)을 계산하여 반환합니다.
   */
  public long getExpiration(String accessToken) {
    try {
      // 토큰의 Claims 추출
      Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken).getPayload();

      // 만료 시간(Expiration) 추출
      Date expiration = claims.getExpiration();

      // 현재 시간과의 차이 계산 (남은 시간 ms)
      long now = System.currentTimeMillis();
      return Math.max(0, expiration.getTime() - now);
    } catch (Exception e) {
      return 0;
    }
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public <T> T getClaim(String token, String claimName, Class<T> requiredType) {
    final Claims claims = extractAllClaims(token);
    return claims.get(claimName, requiredType);
  }

  public boolean isReadOnly(String token) {
    Boolean readonly = getClaim(token, "readonly", Boolean.class);
    return readonly != null && readonly;
  }
}