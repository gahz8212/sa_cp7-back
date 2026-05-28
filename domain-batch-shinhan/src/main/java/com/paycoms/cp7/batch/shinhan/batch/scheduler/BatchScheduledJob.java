package com.paycoms.cp7.batch.shinhan.batch.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduledJob extends QuartzJobBean {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    String jobBeanName = context.getMergedJobDataMap().getString("jobBeanName");
    if (jobBeanName == null) {
      throw new JobExecutionException("jobBeanName is not configured in Quartz JobDataMap");
    }

    try {
      Job job = applicationContext.getBean(jobBeanName, Job.class);
      JobParameters params = new JobParametersBuilder()
          .addLong("time", System.currentTimeMillis())
          .toJobParameters();
      jobLauncher.run(job, params);
    } catch (Exception e) {
      throw new JobExecutionException("Failed to launch batch job [" + jobBeanName + "]", e, false);
    }
  }
}