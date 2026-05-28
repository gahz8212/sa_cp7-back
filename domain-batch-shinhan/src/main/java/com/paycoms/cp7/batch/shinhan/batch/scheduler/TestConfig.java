package com.paycoms.cp7.batch.shinhan.batch.scheduler;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.paycoms.cp7.batch.shinhan.domain.RepeatIntervalEntity;
import com.paycoms.cp7.batch.shinhan.dto.RepeatIntervalDto;
import com.paycoms.cp7.batch.shinhan.mapper.SchedulerMapper;

@Configuration
public class TestConfig implements QuartzJobConfig {

  @Autowired
  @Lazy
  private Scheduler scheduler;

  @Autowired
  private SchedulerMapper schedulerMapper;

  private static final String JOB_BEAN_NAME = "testJob";
  private static final String JOB_ID = JOB_BEAN_NAME + "QuartzJob";
  private static final String GROUP = "testGroup";
  private static final String TRIGGER_NAME = "testTrigger";

  @SuppressWarnings("unused")
  private JobDetail createJobDetail() {
    return JobBuilder.newJob(BatchScheduledJob.class)
        .withIdentity(JOB_ID, GROUP)
        .usingJobData("jobBeanName", JOB_BEAN_NAME)
        .storeDurably()
        .build();
  }

  @EventListener(ContextRefreshedEvent.class)
  public void initSchedule() throws SchedulerException {
    initSchedule(JOB_BEAN_NAME);
  }

  public void initSchedule(String jobBeanName) throws SchedulerException {
    if (jobBeanName == null || jobBeanName.isBlank()) {
      jobBeanName = JOB_BEAN_NAME;
    }

    String jobId = jobBeanName + "QuartzJob";
    JobKey jobKey = JobKey.jobKey(jobId, GROUP);
    TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_NAME, GROUP);

    RepeatIntervalEntity entity = schedulerMapper.getRepeatInterval(createDto());
    String cronExpr = entity != null && entity.getCronExpression() != null
        ? entity.getCronExpression()
        : "10 * * * * ?";

    JobDetail jobDetail = createJobDetail(jobBeanName, jobId);
    if (!scheduler.checkExists(jobKey)) {
      scheduler.addJob(jobDetail, true);
    }

    Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity(triggerKey)
        .forJob(jobDetail)
        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpr))
        .build();

    if (scheduler.checkExists(triggerKey)) {
      scheduler.rescheduleJob(triggerKey, trigger);
    } else {
      scheduler.scheduleJob(trigger);
    }
  }

  private JobDetail createJobDetail(String jobBeanName, String jobId) {
    return JobBuilder.newJob(BatchScheduledJob.class)
        .withIdentity(jobId, GROUP)
        .usingJobData("jobBeanName", jobBeanName)
        .storeDurably()
        .build();
  }

  private RepeatIntervalDto createDto() {
    RepeatIntervalDto dto = new RepeatIntervalDto();
    dto.setSchedName("quartzScheduler");
    dto.setTriggerName(TRIGGER_NAME);
    dto.setTriggerGroup(GROUP);
    return dto;
  }

}