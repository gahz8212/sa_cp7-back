package com.paycoms.cp7.batch.shinhan.api.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.paycoms.cp7.batch.shinhan.api.dto.ScheduleRequest;
import com.paycoms.cp7.batch.shinhan.api.model.BatchInfoEntity;
import com.paycoms.cp7.batch.shinhan.api.model.TriggerInfoEntity;

@Mapper
public interface ScheduleMapper {
  TriggerInfoEntity getTriggerInfoById(Long id);

  List<BatchInfoEntity> getSchedules(ScheduleRequest dto);
}