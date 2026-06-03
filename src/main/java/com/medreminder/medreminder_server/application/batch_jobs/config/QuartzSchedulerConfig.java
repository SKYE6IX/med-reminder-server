package com.medreminder.medreminder_server.application.batch_jobs.config;


import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.MedicationScheduleEventScheduler;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .withIdentity("medicationScheduleEventScheduler")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger medicationTrigger(@Qualifier("medicationEventSchedulerJobDetail") JobDetail detail) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity("medicationScheduleEventTrigger")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 1 * * ?")
                        .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }
}