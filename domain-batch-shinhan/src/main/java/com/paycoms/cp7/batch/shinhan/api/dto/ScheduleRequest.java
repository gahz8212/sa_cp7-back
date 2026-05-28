package com.paycoms.cp7.batch.shinhan.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "스케줄 리스트 요청 DTO")
public class ScheduleRequest {
  @Schema(description = "상태값", nullable = true)
  private String[] states;

  @Schema(description = "조회조건", nullable = true)
  private String searchType;

  @Schema(description = "조회값", nullable = true)
  private String searchValue;
}
