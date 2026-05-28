package com.paycoms.cp7.batch.shinhan.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "스케줄 상태값 변경 요청 DTO")
public class ScheduleStatusRequest {
  @Schema(description = "배치ID", nullable = false)
  @NotNull(message = "VALI_001.id")
  private Long id;

  @Schema(description = "상태값(WAITING, PAUSED, RESCHEDULED)", nullable = false)
  @NotBlank(message = "VALI_001.trgStt")
  private String trgStt;

  @Schema(description = "변경될 Cron 표현식(0 * * * * ?)", nullable = true)
  private String cronExp; // RESCHEDULED 상태에서 사용할 새로운 Cron 표현식
}
