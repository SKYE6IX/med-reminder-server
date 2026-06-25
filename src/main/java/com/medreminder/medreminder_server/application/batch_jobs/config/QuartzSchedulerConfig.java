package com.medreminder.medreminder_server.application.batch_jobs.config;


import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.*;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.quartz.autoconfigure.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.TimeZone;

@Configuration
public class QuartzSchedulerConfig {

    private final String timeZone = "Europe/Moscow";

    @Bean
    public SchedulerFactoryBeanCustomizer customizer() {
        return factory -> factory.setJobFactory(new SpringBeanJobFactory());
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
                        .inTimeZone(TimeZone.getTimeZone(timeZone)))
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
                        .inTimeZone(TimeZone.getTimeZone(timeZone)))
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
                        .inTimeZone(TimeZone.getTimeZone(timeZone)))
                .build();
    }

    @Bean
    public JobDetail markMissedDosageJobDetail() {
        return JobBuilder.newJob(MarkMissedDosageScheduler.class)
                .withIdentity("mark_missed_dosage_job_detail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger markMissedDosageTrigger(@Qualifier("markMissedDosageJobDetail") JobDetail detail) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity("mark_missed_dosage_trigger")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 3 * * ?")
                        .inTimeZone(TimeZone.getTimeZone(timeZone)))
                .build();
    }

    @Bean
    public JobDetail purgeStaleTokenJobDetail() {
        return JobBuilder.newJob(PurgeStaleTokenScheduler.class)
                .withIdentity("purge_stale_token_job_detail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger purgeStaleTokenTrigger(@Qualifier("purgeStaleTokenJobDetail") JobDetail detail) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity("purge_stale_token_trigger")
                .withSchedule(
                        CronScheduleBuilder
                                .cronSchedule("0 0 6 * * ?")
                                .inTimeZone(TimeZone.getTimeZone(timeZone)))
                .build();
    }
}