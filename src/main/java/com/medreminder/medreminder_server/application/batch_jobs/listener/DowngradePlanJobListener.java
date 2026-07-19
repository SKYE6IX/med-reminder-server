package com.medreminder.medreminder_server.application.batch_jobs.listener;

import com.medreminder.medreminder_server.application.services.TelemetryService;
import io.sentry.SentryAttribute;
import io.sentry.SentryLogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class DowngradePlanJobListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(DowngradePlanJobListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            long count = jobExecution.getStepExecutions().stream()
                    .mapToLong(StepExecution::getWriteCount).sum();
            log.info("Downgrade Plan job completed. {} total plan downgraded.", count);

            TelemetryService.log(
                    SentryLogLevel.INFO,
                    "Downgrade Plan job completed.",
                    SentryAttribute.integerAttribute("TotalPlanDowngraded", (int) count)
            );


        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Downgrade Plan job FAILED: {}", jobExecution.getAllFailureExceptions());

            TelemetryService.log(
                    SentryLogLevel.ERROR,
                    "Downgrade Plan job FAILED.",
                    SentryAttribute.named("FailureExceptions", jobExecution.getAllFailureExceptions())
            );
        }
    }
}
