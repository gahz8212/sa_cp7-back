package com.paycoms.cp7.batch.shinhan.api.service;

import java.util.List;
import org.quartz.CronScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paycoms.cp7.global.util.Utils;
import com.paycoms.cp7.batch.shinhan.api.dto.ScheduleRequest;
import com.paycoms.cp7.batch.shinhan.api.dto.ScheduleResponse;
import com.paycoms.cp7.batch.shinhan.api.mapper.ScheduleMapper;
import com.paycoms.cp7.batch.shinhan.api.mapper.struct.ScheduleStructMapper;
import com.paycoms.cp7.batch.shinhan.api.model.BatchInfoEntity;
import com.paycoms.cp7.batch.shinhan.api.model.TriggerInfoEntity;
import com.paycoms.cp7.global.error.BusinessException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

  @Autowired
  private Scheduler scheduler;

  private final ScheduleMapper scheduleMapper;
  private final ScheduleStructMapper scheduleStructMapper;

  public enum TriggerState {
    // WAITING, ACQUIRED, EXECUTING, PAUSED, BLOCKED, COMPLETE, ERROR, DELETED;
    // 실제로는 WAITING과 EXECUTING만 사용, 나머지는 향후 필요 시 추가 예정
    WAITING, PAUSED, RESCHEDULED;

    // 문자열이 Enum에 존재하는지 체크하는 메서드
    public static boolean isValid(String state) {
      if (state == null)
        return false;
      for (TriggerState ts : TriggerState.values()) {
        if (ts.name().equals(state.toUpperCase())) {
          return true;
        }
      }
      return false;
    }
  }

  public void updateStatus(Long id, String triggerState, String cronExpression) {
    if (!TriggerState.isValid(triggerState)) {
      throw new BusinessException("SCH_001", triggerState);
    }

    TriggerInfoEntity triggerInfo = scheduleMapper.getTriggerInfoById(id);

    if (Utils.isEmpty(triggerInfo)) {
      throw new BusinessException("SCH_003", id);
    }

    TriggerKey triggerKey = TriggerKey.triggerKey(triggerInfo.getTriggerName(), triggerInfo.getTriggerGroup());

    try {
      switch (TriggerState.valueOf(triggerState.toUpperCase())) {
        case WAITING:
          scheduler.resumeTrigger(triggerKey);
          break;
        // case ACQUIRED:
        case PAUSED:
          scheduler.pauseTrigger(triggerKey);
          break;
        case RESCHEDULED:
          if (Utils.isEmpty(cronExpression)) {
            throw new BusinessException("SCH_002");
          }

          try {
            Trigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey) // 기존과 동일한 키를 사용
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)) // 새로운 Cron 설정
                .build();

            scheduler.rescheduleJob(triggerKey, newTrigger);
          } catch (Exception e) {
            throw new BusinessException("SCH_004", cronExpression);
          }

          break;
      }
    } catch (SchedulerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public List<ScheduleResponse> getSchedules(ScheduleRequest request) {
    List<BatchInfoEntity> batchInfoList = scheduleMapper.getSchedules(request);
    return scheduleStructMapper.toDtoList(batchInfoList);
  }
}
