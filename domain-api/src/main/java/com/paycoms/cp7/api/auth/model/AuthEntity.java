package com.paycoms.cp7.api.auth.model;

import lombok.*;
import org.apache.ibatis.type.Alias;
import com.paycoms.cp7.global.common.BaseTimeEntity;

@Getter
@Setter
@NoArgsConstructor // MyBatis가 객체를 생성할 때 필요함
@AllArgsConstructor // @Builder 사용 시 필요
@Builder
@Alias("AuthEntity") // XML에서 resultType="Auth"로 쓸 수 있게 해줌
public class AuthEntity extends BaseTimeEntity {
    private String id;
    private String email;
    private String name;
}