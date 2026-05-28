package com.paycoms.cp7.global.auth.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
  AuthPolicy value() default AuthPolicy.AUTHENTICATED;
}