package com.paycoms.cp7.batch.shinhan.batch.job;

import com.paycoms.cp7.batch.shinhan.batch.processor.TestProcessor;
import com.paycoms.cp7.batch.shinhan.domain.TestEntity;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TestJobConfig {
  @Autowired
  private TestProcessor testProcessor; // 생성한 프로세서 주입

  @SuppressWarnings("null")
  @Bean
  public Job testJob(JobRepository jobRepository, Step testStep) {
    return new JobBuilder("testJob", jobRepository)
        .start(testStep)
        .build();
  }

  @SuppressWarnings("null")
  @Bean
  public Step sampleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
      MyBatisPagingItemReader<TestEntity> reader,
      MyBatisBatchItemWriter<TestEntity> writer) {
    return new StepBuilder("testStep", jobRepository)
        .<TestEntity, TestEntity>chunk(10, transactionManager)
        .reader(reader)
        .processor(testProcessor)
        .writer(writer)
        .build();
  }

  @Bean
  @StepScope // <-- 반드시 추가!
  public MyBatisPagingItemReader<TestEntity> reader(SqlSessionFactory sqlSessionFactory) {
    return new MyBatisPagingItemReaderBuilder<TestEntity>()
        .sqlSessionFactory(sqlSessionFactory)
        .queryId("com.paycoms.cp7.batch.mapper.TestMapper.findAll")
        .pageSize(10)
        .build();
  }

  @Bean
  public MyBatisBatchItemWriter<TestEntity> writer(SqlSessionFactory sqlSessionFactory) {
    return new MyBatisBatchItemWriterBuilder<TestEntity>()
        .sqlSessionFactory(sqlSessionFactory)
        .statementId("com.paycoms.cp7.batch.mapper.TestMapper.updateStatus")
        .build();
  }
}