package com.paycoms.cp7.batch.shinhan.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.paycoms.cp7.batch.shinhan.domain.TestEntity;

@Mapper
public interface TestMapper {
  List<TestEntity> findAll();

  void updateStatus(TestEntity model);
}
