package com.medreminder.medreminder_server.batch_jobs.jobs;


import com.medreminder.medreminder_server.TestConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.MarkMissedDosageBatchConfig;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.repository.medications.JpaScheduleEventRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBatchTest
@SpringJUnitConfig(classes = {
        TestConfig.class,
        MarkMissedDosageBatchConfig.class
})
@ActiveProfiles("test")
public class MarkMissedDosageJobUnitTest {

    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    @Qualifier("markMissedDosageJob")
    private Job markMissedDosageJob;

    @Autowired
    private JpaScheduleEventRepo scheduleEventRepo;

    @BeforeEach
    void setup() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void jobShouldProcessOnlyEligibleAndCompleteSuccessfully() throws Exception{

        LocalDate now = LocalDate.now();
        LocalDateTime yesterday = now
                .minusDays(1).atTime(6,30,0);

        ScheduleEventEntity scheduleEventEntity = new ScheduleEventEntity(
                null,
                new BigDecimal("2.1"),
                "PENDING",
                yesterday,
                null
        );
        scheduleEventRepo.save(scheduleEventEntity);

        jobOperatorTestUtils.setJob(markMissedDosageJob);
        JobExecution execution = jobOperatorTestUtils.startJob(
                new JobParametersBuilder()
                        .addLocalDate("runDate", LocalDate.now())
                        .toJobParameters()
        );

        StepExecution stepExecution = execution.getStepExecutions().iterator().next();
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(stepExecution.getReadCount()).isEqualTo(1);
        assertThat(stepExecution.getWriteCount()).isEqualTo(1);
    }
}
