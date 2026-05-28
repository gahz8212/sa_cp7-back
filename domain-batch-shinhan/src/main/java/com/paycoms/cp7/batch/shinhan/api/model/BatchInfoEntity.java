package com.paycoms.cp7.batch.shinhan.api.model;

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
@Alias("BatchInfoEntity") // XML에서 resultType="BatchInfoEntity"로 쓸 수 있게 해줌
public class BatchInfoEntity {
  private Long batchId;
  private String groupNm;
  private String batchNm;
  private String triggerState;
  private String triggerStateDesc;
  private String cronExpression;
}
