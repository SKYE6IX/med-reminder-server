package com.medreminder.medreminder_server.batch_jobs.config;


import com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler.RenewPaidPlanScheduler;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.TimeZone;

@Configuration
public class RenewPaidPlanSchedulerTestConfig {

    @Bean
    public SchedulerFactoryBean renewPaidPlanSchedulerFactoryBean(
            ApplicationContext context,
            JobDetail renewPaidPlanJobDetail,
            Trigger renewPaidPlanTrigger) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setJobFactory(new SpringBeanJobFactory());
        factory.setOverwriteExistingJobs(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);

        factory.setJobDetails(renewPaidPlanJobDetail);
        factory.setTriggers(renewPaidPlanTrigger);
        return factory;
    };

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
