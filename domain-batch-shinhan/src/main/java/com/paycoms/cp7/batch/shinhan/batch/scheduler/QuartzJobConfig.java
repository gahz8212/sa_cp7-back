package com.paycoms.cp7.batch.shinhan.batch.scheduler;

import org.quartz.SchedulerException;

public interface QuartzJobConfig {
  void initSchedule(String jobBeanName) throws SchedulerException;
}
