package com.medreminder.medreminder_server.application.batch_jobs.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class RenewPaidPlanJobListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(RenewPaidPlanJobListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            long count = jobExecution.getStepExecutions().stream()
                    .mapToLong(StepExecution::getWriteCount).sum();

            log.info("Renew Paid Plan job completed. {} total subscription renewed", count);

        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Renew Paid Plan job FAILED: {}", jobExecution.getAllFailureExceptions());
            // Send alert to Slack / PagerDuty / email here
        }
    }
}
