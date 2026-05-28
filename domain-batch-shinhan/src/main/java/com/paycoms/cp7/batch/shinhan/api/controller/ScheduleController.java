package com.paycoms.cp7.batch.shinhan.api.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.paycoms.cp7.batch.shinhan.api.dto.ScheduleRequest;
import com.paycoms.cp7.batch.shinhan.api.dto.ScheduleResponse;
import com.paycoms.cp7.batch.shinhan.api.dto.ScheduleStatusRequest;
import com.paycoms.cp7.batch.shinhan.api.service.ScheduleService;
import com.paycoms.cp7.global.common.ApiResponse;
import com.paycoms.cp7.global.util.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Schedule Controller", description = "스케줄러 관련 API")
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {
  private final MessageUtils messageUtils;
  private final ScheduleService scheduleService;

  @Operation(summary = "스케줄러 조회", description = "스케줄러 목록을 조회합니다.")
  @GetMapping("/get-schedules")
  public ApiResponse<List<ScheduleResponse>> getSchedules(@Valid @ModelAttribute ScheduleRequest request) {
    List<ScheduleResponse> schedules = scheduleService.getSchedules(request);
    return messageUtils.createResponse("SYS_200", schedules);
  }

  @Operation(summary = "스케줄러 상태 변경", description = "변경된 상태로 반영합니다.")
  @PostMapping("/update-status")
  public ApiResponse<Object> updateStatus(@Valid @RequestBody ScheduleStatusRequest request) {
    Long id = request.getId();
    String triggerState = request.getTrgStt();
    String cronExpression = request.getCronExp();

    scheduleService.updateStatus(id, triggerState, cronExpression);

    return messageUtils.createResponse("SYS_200", null);
  }
}
