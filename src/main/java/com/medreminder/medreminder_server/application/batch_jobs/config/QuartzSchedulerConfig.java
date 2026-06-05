package com.medreminder.medreminder_server.application.batch_jobs.config;


import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.DowngradePlanScheduler;
import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.MedicationScheduleEventScheduler;
import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.RenewPaidPlanScheduler;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.TimeZone;

@Configuration
public class QuartzSchedulerConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            DataSource dataSource,
            ApplicationContext context) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setJobFactory(new SpringBeanJobFactory());
        factory.setOverwriteExistingJobs(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        return factory;
    }

    @Bean
    public JobDetail medicationEventSchedulerJobDetail() {
        return JobBuilder.newJob(MedicationScheduleEventScheduler.class)
                .withIdentity("medication_schedule_event_scheduler")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger medicationTrigger(@Qualifier("medicationEventSchedulerJobDetail") JobDetail detail) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity("medication_schedule_event_trigger")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 1 * * ?")
                        .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }

    @Bean
    public JobDetail downgradePlanJobDetail() {
        return JobBuilder.newJob(DowngradePlanScheduler.class)
                .withIdentity("downgrade_plan_job_detail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger downgradePlanTrigger(@Qualifier("downgradePlanJobDetail") JobDetail detail) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity("downgrade_plan_trigger")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 2 * * ?")
                        .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }

    @Bean
    public JobDetail renewPaidPlanJobDetail() {
        return JobBuilder.newJob(RenewPaidPlanScheduler.class)
                .withIdentity("renew_paid_plan_job_detail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger renewPaidPlanTrigger(@Qualifier("renewPaidPlanJobDetail") JobDetail detail) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity("renew_paid_plan_trigger")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 7 * * ?")
                        .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }
}