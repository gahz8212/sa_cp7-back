package com.paycoms.cp7.global.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 파라미터 위치에만 사용하겠다
@Retention(RetentionPolicy.RUNTIME) // 실행 시점까지 유지하겠다
public @interface LoginUser {
}