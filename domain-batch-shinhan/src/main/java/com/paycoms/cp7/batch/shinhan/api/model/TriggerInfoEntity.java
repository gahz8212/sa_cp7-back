package com.paycoms.cp7.batch.shinhan.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "트리거정보 Entity")
public class TriggerInfoEntity {
  @Schema(description = "트리거명")
  private String triggerName;

  @Schema(description = "트리거그룹")
  private String triggerGroup;
}
