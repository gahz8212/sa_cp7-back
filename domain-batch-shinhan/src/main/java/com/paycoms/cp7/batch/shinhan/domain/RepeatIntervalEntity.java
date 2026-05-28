package com.paycoms.cp7.batch.shinhan.domain;

import org.apache.ibatis.type.Alias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // MyBatis가 객체를 생성할 때 필요함
@AllArgsConstructor // @Builder 사용 시 필요
@Builder
@Alias("RepeatIntervalEntity") // XML에서 resultType="Auth"로 쓸 수 있게 해줌
public class RepeatIntervalEntity {
  private String cronExpression;
}
