package com.paycoms.cp7.global.auth.annotation;

public enum AuthPolicy {
  PUBLIC, // 인증 필요 없음
  AUTHENTICATED, // 일반 인증 유저
  READ_WRITE // 인증 유저 + ReadOnly 클레임이 false여야 함
}