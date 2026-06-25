package com.medreminder.medreminder_server.batch_jobs.config;


import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.MarkMissedDosageScheduler;
import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.PurgeStaleTokenScheduler;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.TimeZone;

@Configuration
public class PurgeStaleTokenSchedulerTestConfig {

    @Bean
    public SchedulerFactoryBean purgeStaleTokenSchedulerFactoryBean(
            ApplicationContext context,
            JobDetail purgeStaleTokenSchedulerJobDetail,
            Trigger purgeStaleTokenTrigger) {

        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setJobFactory(new SpringBeanJobFactory());
        factory.setOverwriteExistingJobs(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);

        factory.setJobDetails(purgeStaleTokenSchedulerJobDetail);
        factory.setTriggers(purgeStaleTokenTrigger);
        return factory;
    }

    @Bean
    public JobDetail purgeStaleTokenSchedulerJobDetail() {
        return JobBuilder.newJob(PurgeStaleTokenScheduler.class)
                .withIdentity("purge_stale_token_job_detail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger purgeStaleTokenTrigger(
            @Qualifier("purgeStaleTokenSchedulerJobDetail") JobDetail detail) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity("purge_stale_token_trigger")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 6 * * ?")
                        .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }
}
