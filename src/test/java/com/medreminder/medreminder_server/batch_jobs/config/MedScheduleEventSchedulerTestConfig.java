package com.medreminder.medreminder_server.batch_jobs.config;


import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.MedicationScheduleEventScheduler;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.TimeZone;

@Configuration
public class MedScheduleEventSchedulerTestConfig {

    @Bean
    public SchedulerFactoryBean medScheduleEventSchedulerFactoryBean(
            ApplicationContext context,
            JobDetail medicationEventSchedulerJobDetail,
            Trigger medicationTrigger) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setJobFactory(new SpringBeanJobFactory());
        factory.setOverwriteExistingJobs(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);

        factory.setJobDetails(medicationEventSchedulerJobDetail);
        factory.setTriggers(medicationTrigger);
        return factory;
    }

    @Bean
    public JobDetail medicationEventSchedulerJobDetail() {
        return JobBuilder.newJob(MedicationScheduleEventScheduler.class)
                .withIdentity("medication_schedule_event_job_detail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger medicationTrigger(
            @Qualifier("medicationEventSchedulerJobDetail") JobDetail detail) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity("medication_schedule_event_trigger")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 1 * * ?")
                        .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }
}
