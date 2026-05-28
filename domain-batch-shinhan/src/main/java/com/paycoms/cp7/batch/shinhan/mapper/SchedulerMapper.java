package com.paycoms.cp7.batch.shinhan.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.paycoms.cp7.batch.shinhan.domain.RepeatIntervalEntity;
import com.paycoms.cp7.batch.shinhan.dto.RepeatIntervalDto;

@Mapper
public interface SchedulerMapper {
  RepeatIntervalEntity getRepeatInterval(RepeatIntervalDto dto);
}