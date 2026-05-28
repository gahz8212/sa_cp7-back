package com.paycoms.cp7.batch.shinhan.api.mapper.struct;

import com.paycoms.cp7.batch.shinhan.api.model.BatchInfoEntity;
import com.paycoms.cp7.batch.shinhan.api.dto.ScheduleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring") // 스프링 빈으로 등록
public interface ScheduleStructMapper {
    // 필드명이 다를 때 Mapping을 설정합니다.
    @Mapping(source = "batchId", target = "id")
    @Mapping(source = "groupNm", target = "groupName")
    @Mapping(source = "batchNm", target = "batchName")
    @Mapping(source = "triggerState", target = "trgStt")
    @Mapping(source = "triggerStateDesc", target = "trgSttDesc")
    @Mapping(source = "cronExpression", target = "cronExp")
    ScheduleResponse toDto(BatchInfoEntity entity);

    // 리스트 변환 메서드 (MapStruct가 위 toDto를 반복 실행함)
    List<ScheduleResponse> toDtoList(List<BatchInfoEntity> entities);
}