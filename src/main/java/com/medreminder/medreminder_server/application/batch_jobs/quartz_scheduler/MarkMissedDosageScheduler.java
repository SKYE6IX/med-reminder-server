package com.medreminder.medreminder_server.application.batch_jobs.quartz_scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDate;

public class MarkMissedDosageScheduler extends QuartzJobBean {

    @Autowired
    JobOperator jobOperator;

    @Autowired
    @Qualifier("markMissedDosageJob")
    private Job markMissedDosageJob;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobParameters params = new JobParametersBuilder()
                .addLocalDate("runDate", LocalDate.now())
                .toJobParameters();
        try {
            jobOperator.start(markMissedDosageJob, params);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
