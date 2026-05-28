package com.paycoms.cp7.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass // 이 클래스를 상속받는 엔티티들이 아래 필드들을 컬럼으로 인식하게 함
@EntityListeners(AuditingEntityListener.class) // 자동으로 날짜를 입력해주는 기능
public abstract class BaseTimeEntity {

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;
}