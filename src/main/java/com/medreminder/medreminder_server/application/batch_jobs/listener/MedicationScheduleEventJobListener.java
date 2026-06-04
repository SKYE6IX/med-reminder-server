package com.medreminder.medreminder_server.application.batch_jobs.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;


@Component
public class MedicationScheduleEventJobListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(MedicationScheduleEventJobListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            long count = jobExecution.getStepExecutions().stream()
                    .mapToLong(StepExecution::getWriteCount).sum();

            log.info("Medication Schedule Event job completed. {} created coming days events.", count);

        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Medication Schedule Event job FAILED: {}", jobExecution.getAllFailureExceptions());
            // Send alert to Slack / PagerDuty / email here
        }
    }
}
