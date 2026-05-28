package com.paycoms.cp7.batch.shinhan.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "스케줄 리스트 응답 DTO")
public class ScheduleResponse {
  @Schema(description = "배치ID")
  private Long id;

  @Schema(description = "그룹명")
  private String groupName;

  @Schema(description = "배치명")
  private String batchName;

  @Schema(description = "상태값")
  private String trgStt;

  @Schema(description = "상태 설명")
  private String trgSttDesc;

  @Schema(description = "Cron 표현식")
  private String cronExp;
}
