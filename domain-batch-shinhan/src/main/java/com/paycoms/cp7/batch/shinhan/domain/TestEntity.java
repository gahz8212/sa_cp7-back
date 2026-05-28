package com.paycoms.cp7.batch.shinhan.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DB의 sample_table과 매핑되는 도메인 모델 클래스입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity {

  /**
   * 고유 식별자 (Primary Key)
   */
  private Long id;

  /**
   * 데이터 이름
   */
  private String name;

  /**
   * 처리 상태 (예: READY, DONE)
   */
  private String status;
}