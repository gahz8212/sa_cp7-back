package com.paycoms.cp7.batch.shinhan.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.paycoms.cp7.batch.shinhan.domain.TestEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * Reader에서 읽어온 SampleModel을 가공하는 프로세서입니다.
 * ItemProcessor<입력타입, 출력타입> 형식을 가집니다.
 */
@Slf4j
@Component
public class TestProcessor implements ItemProcessor<TestEntity, TestEntity> {

  @Override
  public TestEntity process(TestEntity item) throws Exception {
    // 예시: 이름을 대문자로 변환하는 비즈니스 로직
    String processedName = item.getName().toUpperCase();
    item.setName(processedName);

    log.info("Processing item: {}", item);

    // 만약 특정 조건에서 null을 반환하면, 해당 데이터는 Writer로 넘어가지 않고 필터링됩니다.
    if ("SKIP".equals(item.getStatus())) {
      return null;
    }

    return item;
  }
}