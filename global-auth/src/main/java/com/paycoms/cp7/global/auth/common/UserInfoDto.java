package com.paycoms.cp7.global.auth.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserInfoDto {
  String id;
  String name;
  String serviceType;
  boolean readOnly;
}
