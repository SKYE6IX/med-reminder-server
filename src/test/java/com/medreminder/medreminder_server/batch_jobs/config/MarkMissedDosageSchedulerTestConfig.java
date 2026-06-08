package com.medreminder.medreminder_server.batch_jobs.config;


import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.MarkMissedDosageScheduler;
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
public class MarkMissedDosageSchedulerTestConfig {

    @Bean
    public SchedulerFactoryBean markMissedDosageSchedulerFactoryBean(
            ApplicationContext context,
            JobDetail markMissedDosageSchedulerJobDetail,
            Trigger markMissedDosageTrigger) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setJobFactory(new SpringBeanJobFactory());
        factory.setOverwriteExistingJobs(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);

        factory.setJobDetails(markMissedDosageSchedulerJobDetail);
        factory.setTriggers(markMissedDosageTrigger);
        return factory;
    }


    @Bean
    public JobDetail markMissedDosageSchedulerJobDetail() {
        return JobBuilder.newJob(MarkMissedDosageScheduler.class)
                .withIdentity("mark_missed_dosage_job_detail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger markMissedDosageTrigger(
            @Qualifier("markMissedDosageSchedulerJobDetail") JobDetail detail) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity("mark_missed_dosage_trigger")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 3 * * ?")
                        .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }
}
