package com.medreminder.medreminder_server.batch_jobs.config;


import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.DowngradePlanScheduler;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.TimeZone;

@Configuration
public class DowngradePlanSchedulerTestConfig {

    @Bean
    public SchedulerFactoryBean downgradePlanSchedulerFactoryBean(
            ApplicationContext context,
            JobDetail downgradePlanJobDetail,
            Trigger downgradePlanTrigger) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setJobFactory(new SpringBeanJobFactory());
        factory.setOverwriteExistingJobs(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);

        factory.setJobDetails(downgradePlanJobDetail);
        factory.setTriggers(downgradePlanTrigger);
        return factory;
    };

    @Bean
    public JobDetail downgradePlanJobDetail() {
        return JobBuilder.newJob(DowngradePlanScheduler.class)
                .withIdentity("downgrade_plan_job_detail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger downgradePlanTrigger(
            @Qualifier("downgradePlanJobDetail") JobDetail detail
    ) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity("downgrade_plan_trigger")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 2 * * ?")
                        .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }
}
